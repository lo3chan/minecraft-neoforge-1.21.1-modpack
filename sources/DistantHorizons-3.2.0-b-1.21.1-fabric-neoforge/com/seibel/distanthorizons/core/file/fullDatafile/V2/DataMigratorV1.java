package com.seibel.distanthorizons.core.file.fullDatafile.V2;

import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV1;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.file.fullDatafile.V1.FullDataSourceProviderV1;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataMigratorV1 implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final int MIGRATION_BATCH_COUNT = 10;
   private static final int MIGRATION_MAX_UPDATE_TIMEOUT_IN_MS = 300000;
   private final FullDataUpdaterV2 dataUpdater;
   private boolean migrationStartMessageQueued = false;
   private long legacyDeletionCount = -1L;
   private long migrationCount = -1L;
   private boolean migrationStoppedWithError = false;
   public final AtomicBoolean migrationThreadRunning = new AtomicBoolean(true);
   private final FullDataSourceProviderV1<IDhLevel> v1DataSourceProvider;
   private final String levelId;
   private final File saveDir;

   public DataMigratorV1(FullDataUpdaterV2 dataUpdater, IDhLevel level, String levelId, File saveDir) throws SQLException, IOException {
      this.dataUpdater = dataUpdater;
      this.saveDir = saveDir;
      this.v1DataSourceProvider = new FullDataSourceProviderV1<>(level, saveDir);
      this.levelId = levelId;
      ThreadPoolExecutor executor = ThreadPoolUtil.getFullDataMigrationExecutor();
      if (executor != null) {
         executor.execute(this::convertLegacyDataSources);
      } else {
         LOGGER.error("Unable to start migration for level: [" + this.levelId + "] due to missing executor.");
      }
   }

   private void convertLegacyDataSources() {
      try {
         LOGGER.debug("Attempting to migrate data sources for: [" + this.levelId + "]-[" + this.saveDir + "]...");
         this.migrationThreadRunning.set(true);
         long unusedCount = 0L;
         long totalDeleteCount = this.v1DataSourceProvider.repo.getUnusedDataSourceCount();
         if (totalDeleteCount != 0L) {
            this.showMigrationStartMessage();
            LOGGER.info("deleting [" + this.levelId + "] - [" + totalDeleteCount + "] unused data sources...");
            this.legacyDeletionCount = totalDeleteCount;
            ArrayList<String> unusedDataPosList = this.v1DataSourceProvider.repo.getUnusedDataSourcePositionStringList(50);

            while (unusedDataPosList.size() != 0) {
               unusedCount += unusedDataPosList.size();
               this.legacyDeletionCount = this.legacyDeletionCount - unusedDataPosList.size();
               long startTime = System.currentTimeMillis();
               this.v1DataSourceProvider.repo.deleteUnusedLegacyData(unusedDataPosList);
               unusedDataPosList = this.v1DataSourceProvider.repo.getUnusedDataSourcePositionStringList(50);
               long endStart = System.currentTimeMillis();
               long deleteTime = endStart - startTime;
               LOGGER.info("Deleting [" + this.levelId + "] - [" + unusedCount + "/" + totalDeleteCount + "] in [" + deleteTime + "]ms ...");

               try {
                  Thread.sleep(deleteTime / 2L);
               } catch (InterruptedException var35) {
               }
            }

            LOGGER.info("Done deleting [" + this.levelId + "] - [" + totalDeleteCount + "] unused data sources.");
         }

         long totalMigrationCount = this.v1DataSourceProvider.getDataSourceMigrationCount();
         this.migrationCount = totalMigrationCount;
         LOGGER.debug("Found [" + totalMigrationCount + "] data sources that need migration.");
         ArrayList<FullDataSourceV1> legacyDataSourceList = this.v1DataSourceProvider.getDataSourcesToMigrate(10);
         if (!legacyDataSourceList.isEmpty()) {
            this.showMigrationStartMessage();

            try {
               for (int progressCount = 0;
                  !legacyDataSourceList.isEmpty() && this.migrationThreadRunning.get();
                  this.migrationCount = this.migrationCount - legacyDataSourceList.size()
               ) {
                  NumberFormat numFormat = F3Screen.NUMBER_FORMAT;
                  LOGGER.info(
                     "Migrating [" + this.levelId + "] - [" + numFormat.format((long)progressCount) + "/" + numFormat.format(totalMigrationCount) + "]..."
                  );
                  ArrayList<CompletableFuture<Void>> updateFutureList = new ArrayList<>();

                  for (int i = 0; i < legacyDataSourceList.size() && this.migrationThreadRunning.get(); i++) {
                     FullDataSourceV1 legacyDataSource = legacyDataSourceList.get(i);

                     try {
                        FullDataSourceV2 newDataSource = FullDataSourceV2.createFromLegacyDataSourceV1(legacyDataSource);
                        newDataSource.applyToParent = true;
                        CompletableFuture<Void> future = this.dataUpdater.updateDataSourceAsync(newDataSource);
                        updateFutureList.add(future);
                        future.thenRun(() -> {
                           this.v1DataSourceProvider.repo.deleteWithKey(legacyDataSource.getPos());
                           newDataSource.close();
                        });
                     } catch (Exception var34) {
                        long migrationPos = legacyDataSource.getPos();
                        LOGGER.warn(
                           "Unexpected issue migrating data source at pos [" + DhSectionPos.toString(migrationPos) + "]. Error: " + var34.getMessage(), var34
                        );
                        this.v1DataSourceProvider.markMigrationFailed(migrationPos);
                     }
                  }

                  try {
                     CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(updateFutureList.toArray(new CompletableFuture[0]));
                     combinedFutures.get(300000L, TimeUnit.MILLISECONDS);
                  } catch (TimeoutException | InterruptedException var32) {
                     LOGGER.warn("Migration update timed out after [300000] milliseconds. Migration will re-try the same positions again in a moment.", var32);
                  } catch (ExecutionException var33) {
                     LOGGER.warn("Migration update failed. Migration will re-try the same positions again. Error:" + var33.getMessage(), var33);
                  }

                  legacyDataSourceList = this.v1DataSourceProvider.getDataSourcesToMigrate(10);
                  progressCount += legacyDataSourceList.size();
               }
            } catch (Exception var36) {
               LOGGER.info("migration stopped due to error for: [" + this.levelId + "]-[" + this.saveDir + "], error: [" + var36.getMessage() + "].", var36);
               this.showMigrationEndMessage(false);
               this.migrationStoppedWithError = true;
            } finally {
               if (this.migrationThreadRunning.get()) {
                  LOGGER.info("migration complete for: [" + this.levelId + "]-[" + this.saveDir + "].");
                  this.showMigrationEndMessage(true);
                  this.migrationCount = 0L;
               } else {
                  LOGGER.info("migration stopped for: [" + this.levelId + "]-[" + this.saveDir + "].");
                  this.showMigrationEndMessage(false);
                  this.migrationStoppedWithError = true;
               }
            }
         } else {
            LOGGER.info("No migration necessary.");
         }
      } finally {
         this.migrationThreadRunning.set(false);
      }
   }

   private void showMigrationStartMessage() {
      if (!this.migrationStartMessageQueued) {
         this.migrationStartMessageQueued = true;
         ClientApi.INSTANCE
            .showChatMessageNextFrame(
               "Old Distant Horizons data is being migrated for ["
                  + this.levelId
                  + "]. \nWhile migrating LODs may load slowly \nand DH world gen will be disabled. \nYou can see migration progress in the F3 menu."
            );
      }
   }

   private void showMigrationEndMessage(boolean success) {
      if (success) {
         ClientApi.INSTANCE.showChatMessageNextFrame("Distant Horizons data migration for [" + this.levelId + "] completed.");
      } else {
         ClientApi.INSTANCE
            .showChatMessageNextFrame("Distant Horizons data migration for [" + this.levelId + "] stopped. \nSome data may not have been migrated.");
      }
   }

   public void addDebugMenuStringsToList(List<String> messageList) {
      boolean migrationErrored = this.migrationStoppedWithError;
      if (!migrationErrored) {
         long legacyDeletionCount = this.legacyDeletionCount;
         if (legacyDeletionCount > 0L) {
            messageList.add("  Migrating - Deleting #: " + F3Screen.NUMBER_FORMAT.format(legacyDeletionCount));
         }

         long migrationCount = this.migrationCount;
         if (migrationCount > 0L) {
            messageList.add("  Migrating - Conversion #: " + F3Screen.NUMBER_FORMAT.format(migrationCount));
         }
      } else {
         messageList.add("  Migration Failed");
      }
   }

   @Override
   public void close() {
   }
}
