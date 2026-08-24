package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling.ChunkFileReader_neoforge;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.jetbrains.annotations.Nullable;

public class RegionFileStorageExternalCache_neoforge implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Nullable
   public final RegionFileStorage storage;
   public static final int MAX_CACHE_SIZE = 16;
   public static boolean regionCacheNullPointerWarningSent = false;
   ReentrantLock getRegionFileLock = new ReentrantLock();
   private final ConcurrentLinkedQueue<RegionFileStorageExternalCache$RegionFileCache_neoforge> regionFileCache = new ConcurrentLinkedQueue<>();

   public RegionFileStorageExternalCache_neoforge(RegionFileStorage storage) {
      this.storage = storage;
   }

   @Nullable
   public RegionFile getRegionFile(ChunkPos chunkPos) throws IOException {
      if (this.storage == null) {
         if (!regionCacheNullPointerWarningSent) {
            regionCacheNullPointerWarningSent = true;
            LOGGER.warn(
               "Unable to access Minecraft's chunk cache. This may be due to another mod changing said cache. DH will be unable to access any Minecraft chunk data until said mod is removed."
            );
         }

         return null;
      } else {
         long chunkPosLong = ChunkPos.asLong(chunkPos.getRegionX(), chunkPos.getRegionZ());
         RegionFile regionFile = null;
         int retryCount = 0;
         int maxRetryCount = 8;

         while (retryCount < maxRetryCount) {
            retryCount++;

            try {
               this.getRegionFileLock.lock();
               regionFile = (RegionFile)this.storage.regionCache.getOrDefault(chunkPosLong, null);
               break;
            } catch (ArrayIndexOutOfBoundsException var15) {
               try {
                  Thread.sleep(250L);
               } catch (InterruptedException var14) {
               }
            } catch (NullPointerException var16) {
               if (!regionCacheNullPointerWarningSent) {
                  regionCacheNullPointerWarningSent = true;
                  LOGGER.warn(
                     "Unable to access Minecraft's chunk cache. This may be due to another mod changing said cache. Falling back to DH's internal cache."
                  );
               }
               break;
            } finally {
               this.getRegionFileLock.unlock();
            }
         }

         if (retryCount >= maxRetryCount) {
            ChunkFileReader_neoforge.CHUNK_LOAD_LOGGER.warn("Concurrency issue detected when getting region file for chunk at [" + chunkPos + "].");
         }

         if (regionFile != null) {
            return regionFile;
         } else {
            for (RegionFileStorageExternalCache$RegionFileCache_neoforge cache : this.regionFileCache) {
               if (cache.pos == chunkPosLong) {
                  return cache.file;
               }
            }

            Path storageFolderPath = this.storage.folder;
            if (!Files.exists(storageFolderPath)) {
               return null;
            } else {
               Path regionFilePath = storageFolderPath.resolve("r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ() + ".mca");
               regionFile = new RegionFile(new RegionStorageInfo("level", null, "level type"), regionFilePath, storageFolderPath, false);
               this.regionFileCache.add(new RegionFileStorageExternalCache$RegionFileCache_neoforge(chunkPosLong, regionFile));

               while (this.regionFileCache.size() > 16) {
                  this.regionFileCache.poll().file.close();
               }

               return regionFile;
            }
         }
      }
   }

   @Nullable
   public CompoundTag read(ChunkPos pos) throws IOException {
      RegionFile file = this.getRegionFile(pos);
      if (file == null) {
         return null;
      } else {
         try {
            CompoundTag var4;
            try (DataInputStream stream = file.getChunkDataInputStream(pos)) {
               if (stream == null) {
                  return null;
               }

               var4 = NbtIo.read(stream);
            }

            return var4;
         } catch (Throwable var8) {
            return null;
         }
      }
   }

   @Override
   public void close() throws IOException {
      RegionFileStorageExternalCache$RegionFileCache_neoforge cache;
      while ((cache = this.regionFileCache.poll()) != null) {
         cache.file.close();
      }
   }
}
