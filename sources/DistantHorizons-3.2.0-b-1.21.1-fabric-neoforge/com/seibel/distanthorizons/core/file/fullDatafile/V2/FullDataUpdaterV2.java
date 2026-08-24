package com.seibel.distanthorizons.core.file.fullDatafile.V2;

import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.file.fullDatafile.IDataSourceUpdateListenerFunc;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import com.seibel.distanthorizons.core.util.threading.PositionalLockProvider;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

public class FullDataUpdaterV2 implements IDebugRenderable, AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   protected final PositionalLockProvider updateLockProvider = new PositionalLockProvider();
   public final Set<Long> lockedPosSet = ConcurrentHashMap.newKeySet();
   private final ConcurrentHashMap<Long, AtomicInteger> queuedUpdateCountsByPos = new ConcurrentHashMap<>();
   public final ArrayList<IDataSourceUpdateListenerFunc<FullDataSourceV2>> dateSourceUpdateListeners = new ArrayList<>();
   private final String levelId;
   private final AtomicBoolean isShutdownRef = new AtomicBoolean(false);
   private final FullDataSourceProviderV2 provider;

   public FullDataUpdaterV2(FullDataSourceProviderV2 provider, String levelId) {
      this.provider = provider;
      this.levelId = levelId;
   }

   public CompletableFuture<Void> updateDataSourceAsync(@NotNull FullDataSourceV2 inputDataSource) {
      if (this.isShutdownRef.get()) {
         return CompletableFuture.completedFuture(null);
      } else {
         AbstractExecutorService executor = ThreadPoolUtil.getChunkToLodBuilderExecutor();
         if (executor != null && !executor.isTerminated()) {
            try {
               this.markUpdateStart(inputDataSource.getPos());
               return CompletableFuture.runAsync(
                  () -> {
                     try {
                        this.updateDataSource(inputDataSource);
                     } catch (Exception var6) {
                        LOGGER.error(
                           "Unexpected error in async data source update at pos: ["
                              + DhSectionPos.toString(inputDataSource.getPos())
                              + "], error: ["
                              + var6.getMessage()
                              + "].",
                           var6
                        );
                     } finally {
                        this.markUpdateEnd(inputDataSource.getPos());
                     }
                  },
                  executor
               );
            } catch (RejectedExecutionException var4) {
               this.markUpdateEnd(inputDataSource.getPos());
               return CompletableFuture.completedFuture(null);
            }
         } else {
            return CompletableFuture.completedFuture(null);
         }
      }
   }

   public void updateDataSource(@NotNull FullDataSourceV2 inputData) {
      if (!this.isShutdownRef.get()) {
         long updatePos = inputData.getPos();
         ReentrantLock updateLock = this.updateLockProvider.getLock(updatePos);

         try {
            updateLock.lock();
            this.lockedPosSet.add(updatePos);
            FullDataSourceV2 recipientDataSource = this.provider.get(updatePos);

            try {
               if (recipientDataSource != null) {
                  boolean dataModified = recipientDataSource.updateFromDataSource(inputData);
                  if (dataModified) {
                     FullDataSourceV2DTO dto = this.createDtoFromDataSource(recipientDataSource);

                     try {
                        if (dto != null) {
                           this.provider.repo.save(dto);
                        }
                     } catch (Throwable var21) {
                        if (dto != null) {
                           try {
                              dto.close();
                           } catch (Throwable var20) {
                              var21.addSuppressed(var20);
                           }
                        }

                        throw var21;
                     }

                     if (dto != null) {
                        dto.close();
                     }

                     synchronized (this.dateSourceUpdateListeners) {
                        for (IDataSourceUpdateListenerFunc<FullDataSourceV2> listener : this.dateSourceUpdateListeners) {
                           if (listener != null) {
                              listener.OnDataSourceUpdated(recipientDataSource);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var23) {
               if (recipientDataSource != null) {
                  try {
                     recipientDataSource.close();
                  } catch (Throwable var19) {
                     var23.addSuppressed(var19);
                  }
               }

               throw var23;
            }

            if (recipientDataSource != null) {
               recipientDataSource.close();
            }
         } catch (Exception var24) {
            LOGGER.error("Error updating pos [" + DhSectionPos.toString(updatePos) + "], error: " + var24.getMessage(), var24);
         } finally {
            updateLock.unlock();
            this.lockedPosSet.remove(updatePos);
         }
      }
   }

   private FullDataSourceV2DTO createDtoFromDataSource(FullDataSourceV2 dataSource) {
      try {
         EDhApiDataCompressionMode compressionModeEnum = Config.Common.LodBuilding.dataCompression.get();
         return FullDataSourceV2DTO.CreateFromDataSource(dataSource, compressionModeEnum);
      } catch (IOException var3) {
         LOGGER.warn("Unable to create DTO, error: [" + var3.getMessage() + "].", var3);
         return null;
      }
   }

   private void markUpdateStart(long dataSourcePos) {
      this.queuedUpdateCountsByPos.compute(dataSourcePos, (pos, atomicCount) -> {
         if (atomicCount == null) {
            atomicCount = new AtomicInteger(0);
         }

         atomicCount.incrementAndGet();
         return (AtomicInteger)atomicCount;
      });
   }

   private void markUpdateEnd(long dataSourcePos) {
      this.queuedUpdateCountsByPos.compute(dataSourcePos, (pos, atomicCount) -> {
         if (atomicCount != null && atomicCount.decrementAndGet() <= 0) {
            atomicCount = null;
         }

         return (AtomicInteger)atomicCount;
      });
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer renderer) {
      this.lockedPosSet.forEach(pos -> renderer.renderBox(new AbstractDebugWireframeRenderer.Box(pos, -32.0F, 74.0F, 0.15F, Color.PINK)));
      this.queuedUpdateCountsByPos
         .forEach(
            (pos, updateCountRef) -> renderer.renderBox(
               new AbstractDebugWireframeRenderer.Box(pos, -32.0F, 80.0F + updateCountRef.get() * 16.0F, 0.2F, Color.WHITE)
            )
         );
   }

   @Override
   public void close() {
      this.isShutdownRef.set(true);
   }
}
