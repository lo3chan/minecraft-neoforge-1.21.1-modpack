package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling.ChunkFileReader_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.class_1923;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_2861;
import net.minecraft.class_2867;
import net.minecraft.class_9240;
import org.jetbrains.annotations.Nullable;

public class RegionFileStorageExternalCache_fabric implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Nullable
   public final class_2867 storage;
   public static final int MAX_CACHE_SIZE = 16;
   public static boolean regionCacheNullPointerWarningSent = false;
   ReentrantLock getRegionFileLock = new ReentrantLock();
   private final ConcurrentLinkedQueue<RegionFileStorageExternalCache$RegionFileCache_fabric> regionFileCache = new ConcurrentLinkedQueue<>();

   public RegionFileStorageExternalCache_fabric(class_2867 storage) {
      this.storage = storage;
   }

   @Nullable
   public class_2861 getRegionFile(class_1923 chunkPos) throws IOException {
      if (this.storage == null) {
         if (!regionCacheNullPointerWarningSent) {
            regionCacheNullPointerWarningSent = true;
            LOGGER.warn(
               "Unable to access Minecraft's chunk cache. This may be due to another mod changing said cache. DH will be unable to access any Minecraft chunk data until said mod is removed."
            );
         }

         return null;
      } else {
         long chunkPosLong = class_1923.method_8331(chunkPos.method_17885(), chunkPos.method_17886());
         class_2861 regionFile = null;
         int retryCount = 0;
         int maxRetryCount = 8;

         while (retryCount < maxRetryCount) {
            retryCount++;

            try {
               this.getRegionFileLock.lock();
               regionFile = (class_2861)this.storage.field_17657.getOrDefault(chunkPosLong, null);
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
            ChunkFileReader_fabric.CHUNK_LOAD_LOGGER.warn("Concurrency issue detected when getting region file for chunk at [" + chunkPos + "].");
         }

         if (regionFile != null) {
            return regionFile;
         } else {
            for (RegionFileStorageExternalCache$RegionFileCache_fabric cache : this.regionFileCache) {
               if (cache.pos == chunkPosLong) {
                  return cache.file;
               }
            }

            Path storageFolderPath = this.storage.field_18690;
            if (!Files.exists(storageFolderPath)) {
               return null;
            } else {
               Path regionFilePath = storageFolderPath.resolve("r." + chunkPos.method_17885() + "." + chunkPos.method_17886() + ".mca");
               regionFile = new class_2861(new class_9240("level", null, "level type"), regionFilePath, storageFolderPath, false);
               this.regionFileCache.add(new RegionFileStorageExternalCache$RegionFileCache_fabric(chunkPosLong, regionFile));

               while (this.regionFileCache.size() > 16) {
                  this.regionFileCache.poll().file.close();
               }

               return regionFile;
            }
         }
      }
   }

   @Nullable
   public class_2487 read(class_1923 pos) throws IOException {
      class_2861 file = this.getRegionFile(pos);
      if (file == null) {
         return null;
      } else {
         try {
            class_2487 var4;
            try (DataInputStream stream = file.method_21873(pos)) {
               if (stream == null) {
                  return null;
               }

               var4 = class_2507.method_10627(stream);
            }

            return var4;
         } catch (Throwable var8) {
            return null;
         }
      }
   }

   @Override
   public void close() throws IOException {
      RegionFileStorageExternalCache$RegionFileCache_fabric cache;
      while ((cache = this.regionFileCache.poll()) != null) {
         cache.file.close();
      }
   }
}
