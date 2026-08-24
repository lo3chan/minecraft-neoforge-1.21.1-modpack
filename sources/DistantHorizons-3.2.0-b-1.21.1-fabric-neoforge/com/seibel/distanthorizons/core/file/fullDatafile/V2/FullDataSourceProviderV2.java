package com.seibel.distanthorizons.core.file.fullDatafile.V2;

import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.file.fullDatafile.IDataSourceUpdateListenerFunc;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import com.seibel.distanthorizons.core.sql.repo.FullDataSourceV2Repo;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullDataSourceProviderV2 implements IDebugRenderable, AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final AbstractDebugWireframeRenderer DEBUG_WIREFRAME_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   private static final Set<String> CORRUPT_DATA_ERRORS_LOGGED = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public static final byte ROOT_SECTION_DETAIL_LEVEL = 15;
   public static final byte LEAF_SECTION_DETAIL_LEVEL = 6;
   public final FullDataSourceV2Repo repo;
   protected final AtomicBoolean isShutdownRef = new AtomicBoolean(false);
   protected final File saveDir;
   protected final IDhLevel level;
   protected final String levelId;
   protected final FullDataUpdaterV2 dataUpdater;
   protected final FullDataUpdatePropagatorV2 updatePropagator;
   protected final DataMigratorV1 dataMigratorV1;

   public FullDataSourceProviderV2(IDhLevel level, ISaveStructure saveStructure) throws SQLException, IOException {
      this(level, saveStructure, null);
   }

   public FullDataSourceProviderV2(IDhLevel level, ISaveStructure saveStructure, @Nullable File saveDirOverride) throws SQLException, IOException {
      this.saveDir = saveDirOverride == null ? saveStructure.getSaveFolder(level.getLevelWrapper()) : saveDirOverride;
      this.repo = new FullDataSourceV2Repo("jdbc:dh_sqlite", new File(this.saveDir.getPath() + File.separator + "DistantHorizons.sqlite"));
      this.level = level;
      this.levelId = this.level.getLevelWrapper().getDhIdentifier();
      this.dataUpdater = new FullDataUpdaterV2(this, this.levelId);
      this.updatePropagator = new FullDataUpdatePropagatorV2(this, this.dataUpdater, this.levelId);
      this.dataMigratorV1 = new DataMigratorV1(this.dataUpdater, this.level, this.levelId, this.saveDir);
      DEBUG_WIREFRAME_RENDERER.register(this, Config.Client.Advanced.Debugging.DebugWireframe.showFullDataUpdateStatus);
   }

   public void addDataSourceUpdateListener(IDataSourceUpdateListenerFunc<FullDataSourceV2> listener) {
      synchronized (this.dataUpdater.dateSourceUpdateListeners) {
         this.dataUpdater.dateSourceUpdateListeners.add(listener);
      }
   }

   public void removeDataSourceUpdateListener(IDataSourceUpdateListenerFunc<FullDataSourceV2> listener) {
      synchronized (this.dataUpdater.dateSourceUpdateListeners) {
         this.dataUpdater.dateSourceUpdateListeners.add(listener);
      }
   }

   protected FullDataSourceV2 createDataSourceFromDto(FullDataSourceV2DTO dto) throws InterruptedException, IOException, DataCorruptedException {
      return dto.createDataSource(this.level.getLevelWrapper(), null);
   }

   protected FullDataSourceV2 createAdjDataSourceFromDto(FullDataSourceV2DTO dto, EDhDirection direction) throws InterruptedException, IOException, DataCorruptedException {
      return dto.createDataSource(this.level.getLevelWrapper(), direction);
   }

   public CompletableFuture<FullDataSourceV2> getAsync(long pos) {
      if (this.isShutdownRef.get()) {
         return CompletableFuture.completedFuture(null);
      } else {
         AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
         if (executor != null && !executor.isTerminated()) {
            try {
               return CompletableFuture.supplyAsync(() -> this.get(pos), executor);
            } catch (RejectedExecutionException var5) {
               return CompletableFuture.completedFuture(null);
            }
         } else {
            return CompletableFuture.completedFuture(null);
         }
      }
   }

   @Nullable
   public FullDataSourceV2 get(long pos) {
      if (this.isShutdownRef.get()) {
         return null;
      } else {
         try {
            FullDataSourceV2DTO dto = this.repo.getByKey(pos);

            label130: {
               FullDataSourceV2 dataSource;
               label131: {
                  FullDataSourceV2 var23;
                  try {
                     if (dto == null) {
                        dataSource = FullDataSourceV2.createEmpty(pos);
                        break label131;
                     }

                     dataSource = null;

                     try {
                        dataSource = this.createDataSourceFromDto(dto);
                        if (dto.dataFormatVersion == 1) {
                           EDhApiDataCompressionMode compressionMode = Config.Common.LodBuilding.dataCompression.get();
                           FullDataSourceV2DTO updatedDto = FullDataSourceV2DTO.CreateFromDataSource(dataSource, compressionMode);

                           try {
                              this.repo.save(updatedDto);
                           } catch (Throwable var11) {
                              if (updatedDto != null) {
                                 try {
                                    updatedDto.close();
                                 } catch (Throwable var10) {
                                    var11.addSuppressed(var10);
                                 }
                              }

                              throw var11;
                           }

                           if (updatedDto != null) {
                              updatedDto.close();
                           }
                        }

                        var23 = dataSource;
                     } catch (DataCorruptedException var12) {
                        this.tryLogCorruptedDataError(DhSectionPos.toString(pos), var12);
                        this.repo.deleteWithKey(pos);
                        break label130;
                     } catch (Exception var13) {
                        if (dataSource != null) {
                           dataSource.close();
                        }

                        throw var13;
                     }
                  } catch (Throwable var14) {
                     if (dto != null) {
                        try {
                           dto.close();
                        } catch (Throwable var9) {
                           var14.addSuppressed(var9);
                        }
                     }

                     throw var14;
                  }

                  if (dto != null) {
                     dto.close();
                  }

                  return var23;
               }

               if (dto != null) {
                  dto.close();
               }

               return dataSource;
            }

            if (dto != null) {
               dto.close();
            }
         } catch (InterruptedException var15) {
         } catch (IOException var16) {
            String message = var16.getMessage();
            if (CORRUPT_DATA_ERRORS_LOGGED.add(message)) {
               LOGGER.warn(
                  "File read Error for pos [" + DhSectionPos.toString(pos) + "], this error message will only be logged once, error: [" + message + "].", var16
               );
            }
         } catch (IllegalStateException var17) {
            String messagex = var17.getMessage();
            if (CORRUPT_DATA_ERRORS_LOGGED.add(messagex)) {
               LOGGER.warn(
                  "Incorrectly formatted data for: ["
                     + DhSectionPos.toString(pos)
                     + "], this error message will only be logged once, error: ["
                     + messagex
                     + "].",
                  var17
               );
            }
         } catch (Exception var18) {
            String messagexx = var18.getMessage();
            if (messagexx == null) {
               messagexx = "NULL";
            }

            if (CORRUPT_DATA_ERRORS_LOGGED.add(messagexx)) {
               LOGGER.warn(
                  "Unexpected error getting: [" + DhSectionPos.toString(pos) + "], this error message will only be logged once, error: [" + messagexx + "].",
                  var18
               );
            }
         }

         return null;
      }
   }

   protected void tryLogCorruptedDataError(String whereClause, Exception e) {
      String message = e.getMessage() == null ? e.getMessage() : "No Error message for exception [" + e.getClass().getSimpleName() + "]";
      if (CORRUPT_DATA_ERRORS_LOGGED.add(message)) {
         LOGGER.warn(
            "Corrupted data found at ["
               + whereClause
               + "]. Data at will be deleted so it can be re-generated to prevent issues. Future errors with this same message won't be logged. Error: ["
               + message
               + "].",
            e
         );
      }
   }

   public FullDataSourceV2 getAdjForDirection(long pos, EDhDirection direction) {
      if (this.isShutdownRef.get()) {
         return null;
      } else {
         try {
            FullDataSourceV2DTO dto = this.repo.getAdjByPosAndDirection(pos, direction);

            label87: {
               FullDataSourceV2 migratedDataSource;
               label88: {
                  FullDataSourceV2 var6;
                  label89: {
                     try {
                        if (dto == null) {
                           migratedDataSource = FullDataSourceV2.createEmpty(pos);
                           break label88;
                        }

                        if (dto.dataFormatVersion == 1) {
                           migratedDataSource = this.get(pos);
                           if (migratedDataSource != null) {
                              migratedDataSource.clearAllNonAdjData(direction);
                           }

                           var6 = migratedDataSource;
                           break label89;
                        }

                        try {
                           migratedDataSource = this.createAdjDataSourceFromDto(dto, direction);
                        } catch (DataCorruptedException var8) {
                           this.tryLogCorruptedDataError(DhSectionPos.toString(pos), var8);
                           this.repo.deleteWithKey(pos);
                           break label87;
                        }
                     } catch (Throwable var9) {
                        if (dto != null) {
                           try {
                              dto.close();
                           } catch (Throwable var7) {
                              var9.addSuppressed(var7);
                           }
                        }

                        throw var9;
                     }

                     if (dto != null) {
                        dto.close();
                     }

                     return migratedDataSource;
                  }

                  if (dto != null) {
                     dto.close();
                  }

                  return var6;
               }

               if (dto != null) {
                  dto.close();
               }

               return migratedDataSource;
            }

            if (dto != null) {
               dto.close();
            }
         } catch (InterruptedException var10) {
         } catch (IOException var11) {
            LOGGER.warn("File read Error for pos [" + DhSectionPos.toString(pos) + "], error: " + var11.getMessage(), var11);
         }

         return null;
      }
   }

   public boolean canRetrieveMissingDataSources() {
      return false;
   }

   public boolean canQueueRetrievalNow() {
      return !this.dataMigratorV1.migrationThreadRunning.get();
   }

   @Nullable
   public LongArrayList getPositionsToRetrieve(long pos) {
      return null;
   }

   @Nullable
   public CompletableFuture<DataSourceRetrievalResult> queuePositionForRetrieval(Long genPos) {
      return null;
   }

   public void removeRetrievalRequestIf(DhSectionPos.ICancelablePrimitiveLongConsumer removeIf) {
   }

   public void clearRetrievalQueue() {
   }

   public void setTotalRetrievalPositionCount(int newCount) {
   }

   public void setEstimatedRemainingRetrievalChunkCount(int newCount) {
   }

   public CompletableFuture<Void> updateDataSourceAsync(@NotNull FullDataSourceV2 inputData) {
      return this.dataUpdater.updateDataSourceAsync(inputData);
   }

   @Nullable
   public Long getTimestampForPos(long pos) {
      return this.isShutdownRef.get() ? null : this.repo.getTimestampForPos(pos);
   }

   public void addDebugMenuStringsToList(List<String> messageList) {
      this.dataMigratorV1.addDebugMenuStringsToList(messageList);
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer renderer) {
      this.dataUpdater.debugRender(renderer);
      this.updatePropagator.debugRender(renderer);
   }

   @Override
   public void close() {
      LOGGER.debug("Closing [" + this.getClass().getSimpleName() + "] for level: [" + this.levelId + "].");
      this.isShutdownRef.set(true);
      this.dataUpdater.close();
      this.updatePropagator.close();
      this.dataMigratorV1.close();
      DEBUG_WIREFRAME_RENDERER.unregister(this, Config.Client.Advanced.Debugging.DebugWireframe.showFullDataUpdateStatus);
      this.repo.close();
   }
}
