package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.RegionFileStorageExternalCache_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams_fabric;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.ChunkLightStorage;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_1923;
import net.minecraft.class_2487;
import net.minecraft.class_2839;
import net.minecraft.class_2843;
import net.minecraft.class_2867;
import net.minecraft.class_3218;
import net.minecraft.class_4698;
import net.minecraft.class_7924;

public class ChunkFileReader_fabric implements AutoCloseable {
   public static final DhLogger LOGGER = new DhLoggerBuilder().name("LOD World Gen").fileLevelConfig(Config.Common.Logging.logWorldGenEventToFile).build();
   public static final DhLogger CHUNK_LOAD_LOGGER = new DhLoggerBuilder()
      .name("LOD Chunk Loading")
      .fileLevelConfig(Config.Common.Logging.logWorldGenChunkLoadEventToFile)
      .build();
   private static final IModChecker MOD_CHECKER = SingletonInjector.INSTANCE.get(IModChecker.class);
   public final GlobalWorldGenParams_fabric params;
   private boolean pullExistingChunkUsingMcAsyncMethod = false;
   private final AtomicReference<RegionFileStorageExternalCache_fabric> regionFileStorageCacheRef = new AtomicReference<>();

   public RegionFileStorageExternalCache_fabric getOrCreateRegionFileCache(class_2867 storage) {
      RegionFileStorageExternalCache_fabric cache = this.regionFileStorageCacheRef.get();
      if (cache == null) {
         cache = new RegionFileStorageExternalCache_fabric(storage);
         if (!this.regionFileStorageCacheRef.compareAndSet(null, cache)) {
            cache = this.regionFileStorageCacheRef.get();
         }
      }

      return cache;
   }

   public ChunkFileReader_fabric(GlobalWorldGenParams_fabric params) {
      this.params = params;
      if (MOD_CHECKER.isModLoaded("c2me")) {
         LOGGER.info("C2ME detected: DH's pre-existing chunk accessing will use methods handled by C2ME.");
         this.pullExistingChunkUsingMcAsyncMethod = true;
      }
   }

   public CompletableFuture<ChunkWrapper_fabric> createEmptyOrPreExistingChunkWrapperAsync(
      int chunkX,
      int chunkZ,
      Map<DhChunkPos, ChunkLightStorage> chunkSkyLightingByDhPos,
      Map<DhChunkPos, ChunkLightStorage> chunkBlockLightingByDhPos,
      Map<DhChunkPos, ChunkWrapper_fabric> generatedChunkWrapperByDhPos
   ) {
      class_1923 chunkPos = new class_1923(chunkX, chunkZ);
      DhChunkPos dhChunkPos = new DhChunkPos(chunkX, chunkZ);
      return generatedChunkWrapperByDhPos.containsKey(dhChunkPos)
         ? CompletableFuture.completedFuture(generatedChunkWrapperByDhPos.get(dhChunkPos))
         : this.getChunkNbtDataAsync(chunkPos)
            .thenApply(
               chunkData -> {
                  ChunkWrapper_fabric newChunkWrapper = this.loadOrMakeChunkWrapper(chunkPos, chunkData);
                  ChunkCompoundTagParser$CombinedChunkLightStorage_fabric combinedLights = ChunkCompoundTagParser_fabric.readLight(
                     newChunkWrapper.getChunk(), chunkData
                  );
                  if (combinedLights != null) {
                     chunkSkyLightingByDhPos.put(dhChunkPos, combinedLights.skyLightStorage);
                     chunkBlockLightingByDhPos.put(dhChunkPos, combinedLights.blockLightStorage);
                  }

                  return newChunkWrapper;
               }
            )
            .handle(
               (newChunkWrapper, throwable) -> (ChunkWrapper_fabric)(newChunkWrapper != null
                  ? newChunkWrapper
                  : this.CreateProtoChunkWrapper(this.params.mcServerLevel, chunkPos))
            )
            .thenApply(newChunkWrapper -> {
               generatedChunkWrapperByDhPos.put(dhChunkPos, newChunkWrapper);
               return (ChunkWrapper_fabric)newChunkWrapper;
            });
   }

   private CompletableFuture<class_2487> getChunkNbtDataAsync(class_1923 chunkPos) {
      class_3218 level = this.params.mcServerLevel;

      try {
         class_4698 ioWorker = level.method_14178().field_17254.field_21494;
         if (!this.pullExistingChunkUsingMcAsyncMethod && ioWorker.field_21499 != null) {
            try {
               class_2867 storage = this.params.mcServerLevel.method_14178().field_17254.field_21494.field_21499;
               RegionFileStorageExternalCache_fabric cache = this.getOrCreateRegionFileCache(storage);
               return CompletableFuture.completedFuture(cache.read(chunkPos));
            } catch (NullPointerException var6) {
               LOGGER.error(
                  "Unexpected issue pulling pre-existing chunk [" + chunkPos + "], falling back to async chunk pulling. This may cause server-tick lag.", var6
               );
               this.pullExistingChunkUsingMcAsyncMethod = true;
               return this.getChunkNbtDataAsync(chunkPos);
            }
         } else {
            if (!this.pullExistingChunkUsingMcAsyncMethod) {
               LOGGER.info("Unable to pull pre-existing chunk using synchronous method. Falling back to async method. this may cause server-tick lag.");
               this.pullExistingChunkUsingMcAsyncMethod = true;
            }

            return ioWorker.method_31738(chunkPos)
               .thenApply(optional -> (class_2487)optional.orElse(null))
               .exceptionally(
                  throwable -> {
                     Throwable actualThrowable = throwable;

                     while (actualThrowable instanceof CompletionException) {
                        CompletionException completionException = (CompletionException)actualThrowable;
                        actualThrowable = completionException.getCause();
                     }

                     boolean isShutdownException = ExceptionUtil.isShutdownException(actualThrowable);
                     if (!isShutdownException) {
                        CHUNK_LOAD_LOGGER.warn(
                           "DistantHorizons: Couldn't load or make chunk [" + chunkPos + "], error: [" + actualThrowable.getMessage() + "].", actualThrowable
                        );
                     }

                     return null;
                  }
               );
         }
      } catch (ClosedByInterruptException var7) {
         return CompletableFuture.completedFuture(null);
      } catch (Exception var8) {
         CHUNK_LOAD_LOGGER.warn("Couldn't load or make chunk [" + chunkPos + "]. Error: [" + var8.getMessage() + "].", var8);
         return CompletableFuture.completedFuture(null);
      }
   }

   private ChunkWrapper_fabric loadOrMakeChunkWrapper(class_1923 chunkPos, class_2487 chunkTagData) {
      class_3218 mcServerLevel = this.params.mcServerLevel;
      if (chunkTagData == null) {
         return this.CreateProtoChunkWrapper(mcServerLevel, chunkPos);
      } else {
         try {
            ChunkWrapper_fabric chunkWrapper = ChunkCompoundTagParser_fabric.createFromTag(mcServerLevel, this.params.dhServerLevel, chunkPos, chunkTagData);
            if (chunkWrapper == null) {
               chunkWrapper = this.CreateProtoChunkWrapper(mcServerLevel, chunkPos);
            }

            return chunkWrapper;
         } catch (Exception var5) {
            CHUNK_LOAD_LOGGER.error(
               "DistantHorizons: couldn't load or make chunk at ["
                  + chunkPos
                  + "].Please try optimizing your world to fix this issue. \nWorld optimization can be done from the singleplayer world selection screen.\nError: ["
                  + var5.getMessage()
                  + "].",
               var5
            );
            return this.CreateProtoChunkWrapper(mcServerLevel, chunkPos);
         }
      }
   }

   public ChunkWrapper_fabric CreateProtoChunkWrapper(class_3218 level, class_1923 chunkPos) {
      class_2839 chunk = CreateProtoChunk(level, chunkPos);
      return new ChunkWrapper_fabric(chunk, this.params.dhServerLevel.getLevelWrapper());
   }

   public static class_2839 CreateProtoChunk(class_3218 level, class_1923 chunkPos) {
      return new class_2839(chunkPos, class_2843.field_12950, level, level.method_30349().method_30530(class_7924.field_41236), null);
   }

   @Override
   public void close() {
      RegionFileStorageExternalCache_fabric regionStorage = this.regionFileStorageCacheRef.get();
      if (regionStorage != null) {
         try {
            regionStorage.close();
         } catch (ClosedChannelException var3) {
         } catch (IOException var4) {
            LOGGER.error("Failed to close region file storage cache, error: [" + var4.getMessage() + "].", var4);
         }
      }
   }
}
