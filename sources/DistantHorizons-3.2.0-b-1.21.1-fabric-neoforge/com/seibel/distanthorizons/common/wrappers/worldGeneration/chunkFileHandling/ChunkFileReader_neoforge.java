package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.RegionFileStorageExternalCache_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams_neoforge;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;

public class ChunkFileReader_neoforge implements AutoCloseable {
   public static final DhLogger LOGGER = new DhLoggerBuilder().name("LOD World Gen").fileLevelConfig(Config.Common.Logging.logWorldGenEventToFile).build();
   public static final DhLogger CHUNK_LOAD_LOGGER = new DhLoggerBuilder()
      .name("LOD Chunk Loading")
      .fileLevelConfig(Config.Common.Logging.logWorldGenChunkLoadEventToFile)
      .build();
   private static final IModChecker MOD_CHECKER = SingletonInjector.INSTANCE.get(IModChecker.class);
   public final GlobalWorldGenParams_neoforge params;
   private boolean pullExistingChunkUsingMcAsyncMethod = false;
   private final AtomicReference<RegionFileStorageExternalCache_neoforge> regionFileStorageCacheRef = new AtomicReference<>();

   public RegionFileStorageExternalCache_neoforge getOrCreateRegionFileCache(RegionFileStorage storage) {
      RegionFileStorageExternalCache_neoforge cache = this.regionFileStorageCacheRef.get();
      if (cache == null) {
         cache = new RegionFileStorageExternalCache_neoforge(storage);
         if (!this.regionFileStorageCacheRef.compareAndSet(null, cache)) {
            cache = this.regionFileStorageCacheRef.get();
         }
      }

      return cache;
   }

   public ChunkFileReader_neoforge(GlobalWorldGenParams_neoforge params) {
      this.params = params;
      if (MOD_CHECKER.isModLoaded("c2me")) {
         LOGGER.info("C2ME detected: DH's pre-existing chunk accessing will use methods handled by C2ME.");
         this.pullExistingChunkUsingMcAsyncMethod = true;
      }
   }

   public CompletableFuture<ChunkWrapper_neoforge> createEmptyOrPreExistingChunkWrapperAsync(
      int chunkX,
      int chunkZ,
      Map<DhChunkPos, ChunkLightStorage> chunkSkyLightingByDhPos,
      Map<DhChunkPos, ChunkLightStorage> chunkBlockLightingByDhPos,
      Map<DhChunkPos, ChunkWrapper_neoforge> generatedChunkWrapperByDhPos
   ) {
      ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
      DhChunkPos dhChunkPos = new DhChunkPos(chunkX, chunkZ);
      return generatedChunkWrapperByDhPos.containsKey(dhChunkPos)
         ? CompletableFuture.completedFuture(generatedChunkWrapperByDhPos.get(dhChunkPos))
         : this.getChunkNbtDataAsync(chunkPos)
            .thenApply(
               chunkData -> {
                  ChunkWrapper_neoforge newChunkWrapper = this.loadOrMakeChunkWrapper(chunkPos, chunkData);
                  ChunkCompoundTagParser$CombinedChunkLightStorage_neoforge combinedLights = ChunkCompoundTagParser_neoforge.readLight(
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
               (newChunkWrapper, throwable) -> (ChunkWrapper_neoforge)(newChunkWrapper != null
                  ? newChunkWrapper
                  : this.CreateProtoChunkWrapper(this.params.mcServerLevel, chunkPos))
            )
            .thenApply(newChunkWrapper -> {
               generatedChunkWrapperByDhPos.put(dhChunkPos, newChunkWrapper);
               return (ChunkWrapper_neoforge)newChunkWrapper;
            });
   }

   private CompletableFuture<CompoundTag> getChunkNbtDataAsync(ChunkPos chunkPos) {
      ServerLevel level = this.params.mcServerLevel;

      try {
         IOWorker ioWorker = level.getChunkSource().chunkMap.worker;
         if (!this.pullExistingChunkUsingMcAsyncMethod && ioWorker.storage != null) {
            try {
               RegionFileStorage storage = this.params.mcServerLevel.getChunkSource().chunkMap.worker.storage;
               RegionFileStorageExternalCache_neoforge cache = this.getOrCreateRegionFileCache(storage);
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

            return ioWorker.loadAsync(chunkPos)
               .thenApply(optional -> (CompoundTag)optional.orElse(null))
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

   private ChunkWrapper_neoforge loadOrMakeChunkWrapper(ChunkPos chunkPos, CompoundTag chunkTagData) {
      ServerLevel mcServerLevel = this.params.mcServerLevel;
      if (chunkTagData == null) {
         return this.CreateProtoChunkWrapper(mcServerLevel, chunkPos);
      } else {
         try {
            ChunkWrapper_neoforge chunkWrapper = ChunkCompoundTagParser_neoforge.createFromTag(mcServerLevel, this.params.dhServerLevel, chunkPos, chunkTagData);
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

   public ChunkWrapper_neoforge CreateProtoChunkWrapper(ServerLevel level, ChunkPos chunkPos) {
      ProtoChunk chunk = CreateProtoChunk(level, chunkPos);
      return new ChunkWrapper_neoforge(chunk, this.params.dhServerLevel.getLevelWrapper());
   }

   public static ProtoChunk CreateProtoChunk(ServerLevel level, ChunkPos chunkPos) {
      return new ProtoChunk(chunkPos, UpgradeData.EMPTY, level, level.registryAccess().registryOrThrow(Registries.BIOME), null);
   }

   @Override
   public void close() {
      RegionFileStorageExternalCache_neoforge regionStorage = this.regionFileStorageCacheRef.get();
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
