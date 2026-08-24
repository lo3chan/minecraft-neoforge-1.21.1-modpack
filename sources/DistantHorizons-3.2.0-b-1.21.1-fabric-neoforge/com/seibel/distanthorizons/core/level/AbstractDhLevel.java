package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkModifiedEvent;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.generation.DhLightingEngine;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.render.renderer.CloudRenderHandler;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.sql.dto.ChunkHashDTO;
import com.seibel.distanthorizons.core.sql.repo.BeaconBeamRepo;
import com.seibel.distanthorizons.core.sql.repo.ChunkHashRepo;
import com.seibel.distanthorizons.core.util.KeyedLockContainer;
import com.seibel.distanthorizons.core.util.delayedSaveCache.DelayedBeaconSaveCache;
import com.seibel.distanthorizons.core.util.delayedSaveCache.DelayedDataSourceSaveCache;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDhLevel implements IDhLevel {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Nullable
   public ChunkHashRepo chunkHashRepo;
   @Nullable
   public BeaconBeamRepo beaconBeamRepo;
   protected final KeyedLockContainer<Long> beaconUpdateLockContainer = new KeyedLockContainer<>();
   protected final DelayedDataSourceSaveCache delayedFullDataSourceSaveCache = new DelayedDataSourceSaveCache(this::onDataSourceSaveAsync, 1000);
   protected final DelayedBeaconSaveCache delayedBeaconSaveCache = new DelayedBeaconSaveCache(this::updateBeaconBeamsBetweenBlockPos, 1000);
   protected final ConcurrentHashMap<Long, HashSet<DhChunkPos>> updatedChunkPosSetBySectionPos = new ConcurrentHashMap<>();
   protected final ConcurrentHashMap<DhChunkPos, Integer> updatedChunkHashesByChunkPos = new ConcurrentHashMap<>();
   @Nullable
   protected CloudRenderHandler cloudRenderHandler;

   protected AbstractDhLevel() {
   }

   protected void createAndSetSupportingRepos(File databaseFile) {
      ChunkHashRepo newChunkHashRepo = null;

      try {
         newChunkHashRepo = new ChunkHashRepo("jdbc:dh_sqlite", databaseFile);
      } catch (IOException | SQLException var6) {
         LOGGER.fatal("Unable to create [" + ChunkHashRepo.class.getSimpleName() + "], error: [" + var6.getMessage() + "].", var6);
      }

      this.chunkHashRepo = newChunkHashRepo;
      BeaconBeamRepo newBeaconBeamRepo = null;

      try {
         newBeaconBeamRepo = new BeaconBeamRepo("jdbc:dh_sqlite", databaseFile);
      } catch (IOException | SQLException var5) {
         LOGGER.error("Unable to create [" + BeaconBeamRepo.class.getSimpleName() + "], error: [" + var5.getMessage() + "].", var5);
      }

      this.beaconBeamRepo = newBeaconBeamRepo;
   }

   protected void runRepoReliantSetup() {
      IDhGenericRenderer genericRenderer = this.getGenericRenderer();
      if (genericRenderer != null && this instanceof IDhClientLevel) {
         String enabledCloudDimensions = Config.Client.Advanced.Graphics.GenericRendering.dimensionEnabledCloudRenderingCsv.get();
         String dimName = this.getLevelWrapper().getDimensionType().getName();
         if (enabledCloudDimensions.contains(dimName)) {
            this.cloudRenderHandler = new CloudRenderHandler((IDhClientLevel)this, genericRenderer);
         }
      }
   }

   @Override
   public void updateChunkAsync(IChunkWrapper chunkWrapper, int chunkHash) {
      FullDataSourceV2 dataSource = FullDataSourceV2.createFromChunk(this.getLevelWrapper(), chunkWrapper);

      label40: {
         try {
            if (dataSource == null) {
               break label40;
            }

            this.updatedChunkPosSetBySectionPos.compute(dataSource.getPos(), (dataSourcePos, chunkPosSet) -> {
               if (chunkPosSet == null) {
                  chunkPosSet = new HashSet<>();
               }

               chunkPosSet.add(chunkWrapper.getChunkPos());
               return chunkPosSet;
            });
            this.updatedChunkHashesByChunkPos.put(chunkWrapper.getChunkPos(), chunkHash);
            this.delayedFullDataSourceSaveCache.writeToMemoryAndQueueSave(dataSource);
         } catch (Throwable var7) {
            if (dataSource != null) {
               try {
                  dataSource.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (dataSource != null) {
            dataSource.close();
         }

         return;
      }

      if (dataSource != null) {
         dataSource.close();
      }
   }

   private CompletableFuture<Void> onDataSourceSaveAsync(FullDataSourceV2 fullDataSource) {
      DhLightingEngine.INSTANCE.bakeDataSourceSkyLight(fullDataSource, this.getLevelWrapper().hasSkyLight() ? 15 : 0);
      return this.updateDataSourcesAsync(fullDataSource)
         .thenRun(
            () -> {
               try {
                  HashSet<DhChunkPos> updatedChunkPosSet = this.updatedChunkPosSetBySectionPos.remove(fullDataSource.getPos());
                  if (updatedChunkPosSet != null) {
                     for (DhChunkPos chunkPos : updatedChunkPosSet) {
                        Integer chunkHash = this.updatedChunkHashesByChunkPos.remove(chunkPos);
                        if (this.chunkHashRepo != null && chunkHash != null) {
                           this.chunkHashRepo.save(new ChunkHashDTO(chunkPos, chunkHash));
                        }

                        ApiEventInjector.INSTANCE
                           .fireAllEvents(
                              DhApiChunkModifiedEvent.class, new DhApiChunkModifiedEvent.EventParam(this.getLevelWrapper(), chunkPos.getX(), chunkPos.getZ())
                           );
                     }
                  }
               } catch (Exception var6) {
                  LOGGER.error("Unexpected issue after onDataSourceSaveAsync, error: [" + var6.getMessage() + "].", var6);
               }
            }
         );
   }

   @Override
   public int getChunkHash(DhChunkPos pos) {
      if (this.chunkHashRepo == null) {
         return 0;
      } else {
         ChunkHashDTO dto = this.chunkHashRepo.getByKey(pos);
         return dto != null ? dto.chunkHash : 0;
      }
   }

   @Override
   public void updateBeaconBeamsForSectionPos(long sectionPos, List<BeaconBeamDTO> activeBeamList) {
      int minBlockX = DhSectionPos.getMinCornerBlockX(sectionPos);
      int minBlockZ = DhSectionPos.getMinCornerBlockZ(sectionPos);
      int maxBlockX = minBlockX + DhSectionPos.getBlockWidth(sectionPos);
      int maxBlockZ = minBlockZ + DhSectionPos.getBlockWidth(sectionPos);
      this.updateBeaconBeamsBetweenBlockPos(sectionPos, minBlockX, maxBlockX, minBlockZ, maxBlockZ, activeBeamList);
   }

   @Override
   public void updateBeaconBeamsForChunkPos(DhChunkPos chunkPos, List<BeaconBeamDTO> activeBeamList) {
      this.delayedBeaconSaveCache.queueBeaconBeamUpdatesForChunkPos(chunkPos, activeBeamList);
   }

   private void updateBeaconBeamsBetweenBlockPos(
      long sectionPosForLock, int minBlockX, int maxBlockX, int minBlockZ, int maxBlockZ, List<BeaconBeamDTO> activeBeamList
   ) {
      if (this.beaconBeamRepo != null) {
         ReentrantLock lock = this.beaconUpdateLockContainer.getLockForPos(sectionPosForLock);

         try {
            lock.lock();
            HashSet<DhBlockPos> allPosSet = new HashSet<>();
            HashMap<DhBlockPos, BeaconBeamDTO> activeBeamByPos = new HashMap<>(activeBeamList.size());

            for (BeaconBeamDTO beam : activeBeamList) {
               activeBeamByPos.put(beam.blockPos, beam);
               allPosSet.add(beam.blockPos);
            }

            List<BeaconBeamDTO> existingBeamList = this.beaconBeamRepo.getAllBeamsInBlockPosRange(minBlockX, maxBlockX, minBlockZ, maxBlockZ);
            HashMap<DhBlockPos, BeaconBeamDTO> existingBeamByPos = new HashMap<>(existingBeamList.size());

            for (BeaconBeamDTO beam : existingBeamList) {
               existingBeamByPos.put(beam.blockPos, beam);
               allPosSet.add(beam.blockPos);
            }

            for (DhBlockPos beaconPos : allPosSet) {
               if (minBlockX <= beaconPos.getX() && beaconPos.getX() <= maxBlockX && minBlockZ <= beaconPos.getZ() && beaconPos.getZ() <= maxBlockZ) {
                  BeaconBeamDTO existingBeam = existingBeamByPos.get(beaconPos);
                  BeaconBeamDTO activeBeam = activeBeamByPos.get(beaconPos);
                  if (activeBeam != null) {
                     if (existingBeam == null) {
                        this.beaconBeamRepo.save(activeBeam);
                     } else if (!existingBeam.color.equals(activeBeam.color)) {
                        this.beaconBeamRepo.save(activeBeam);
                     }
                  } else if (existingBeam != null) {
                     this.beaconBeamRepo.deleteWithKey(beaconPos);
                  }
               }
            }
         } finally {
            lock.unlock();
         }
      }
   }

   @Nullable
   @Override
   public BeaconBeamRepo getBeaconBeamRepo() {
      return this.beaconBeamRepo;
   }

   @Override
   public void close() {
      if (this.chunkHashRepo != null) {
         this.chunkHashRepo.close();
      }

      if (this.beaconBeamRepo != null) {
         this.beaconBeamRepo.close();
      }

      this.delayedFullDataSourceSaveCache.close();
   }
}
