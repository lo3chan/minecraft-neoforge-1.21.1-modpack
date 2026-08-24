package com.seibel.distanthorizons.core.file.fullDatafile.V1;

import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV1;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV1DTO;
import com.seibel.distanthorizons.core.sql.repo.FullDataSourceV1Repo;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataInputStream;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.Nullable;

public class FullDataSourceProviderV1<TDhLevel extends IDhLevel> implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("V1DTO");
   protected final ReentrantLock closeLock = new ReentrantLock();
   protected volatile boolean isShutdown = false;
   protected final TDhLevel level;
   protected final File saveDir;
   public final FullDataSourceV1Repo repo;

   public FullDataSourceProviderV1(TDhLevel level, File saveDir) throws SQLException, IOException {
      this.level = level;
      this.saveDir = saveDir;
      if (!this.saveDir.exists() && !this.saveDir.mkdirs()) {
         LOGGER.warn("Unable to create full data folder, file saving may fail.");
      }

      this.repo = new FullDataSourceV1Repo("jdbc:dh_sqlite", new File(this.saveDir.getPath() + File.separator + "DistantHorizons.sqlite"));
   }

   protected FullDataSourceV1 createDataSourceFromDto(FullDataSourceV1DTO dto) throws InterruptedException, IOException, DataCorruptedException {
      FullDataSourceV1 dataSource = FullDataSourceV1.createEmpty(dto.pos);
      PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

      try {
         DhDataInputStream inputStream = DhDataInputStream.create(dto.dataArray, EDhApiDataCompressionMode.LZ4, checkout);

         try {
            dataSource.populateFromStream(dto, inputStream, this.level);
         } catch (Throwable var9) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (Throwable var10) {
         if (checkout != null) {
            try {
               checkout.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (checkout != null) {
         checkout.close();
      }

      return dataSource;
   }

   public CompletableFuture<FullDataSourceV1> getAsync(long pos) {
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

   @Nullable
   public FullDataSourceV1 get(Long pos) {
      FullDataSourceV1 dataSource = null;

      try {
         FullDataSourceV1DTO dto = this.repo.getByKey(pos);

         try {
            if (dto != null) {
               dataSource = this.createDataSourceFromDto(dto);
            }
         } catch (Throwable var7) {
            if (dto != null) {
               try {
                  dto.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (dto != null) {
            dto.close();
         }
      } catch (InterruptedException var8) {
      } catch (DataCorruptedException var9) {
         LOGGER.warn(
            "Corrupted data found at pos ["
               + DhSectionPos.toString(pos)
               + "]. Data at position will be deleted so it can be re-generated and to prevent future issues. Error: "
               + var9.getMessage()
         );
         this.repo.deleteWithKey(pos);
      } catch (IOException var10) {
         LOGGER.warn("File read Error for pos [" + DhSectionPos.toString(pos) + "], error: " + var10.getMessage(), var10);
      }

      return dataSource;
   }

   public long getDataSourceMigrationCount() {
      return this.repo.getMigrationCount();
   }

   public ArrayList<FullDataSourceV1> getDataSourcesToMigrate(int limit) {
      ArrayList<FullDataSourceV1> dataSourceList = new ArrayList<>();
      LongArrayList migrationPosList = this.repo.getPositionsToMigrate(limit);

      for (int i = 0; i < migrationPosList.size(); i++) {
         Long pos = migrationPosList.getLong(i);
         FullDataSourceV1 dataSource = this.get(pos);
         if (dataSource != null) {
            dataSourceList.add(dataSource);
         }
      }

      return dataSourceList;
   }

   public void markMigrationFailed(long pos) {
      this.repo.markMigrationFailed(pos);
   }

   @Override
   public void close() {
      try {
         this.closeLock.lock();
         this.isShutdown = true;
         LOGGER.info("Closing [" + this.getClass().getSimpleName() + "] for level: [" + this.level + "].");
         this.repo.close();
      } finally {
         this.closeLock.unlock();
      }
   }
}
