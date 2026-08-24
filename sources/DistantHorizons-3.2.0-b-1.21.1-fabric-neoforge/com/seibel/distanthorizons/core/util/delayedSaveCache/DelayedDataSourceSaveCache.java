package com.seibel.distanthorizons.core.util.delayedSaveCache;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelayedDataSourceSaveCache extends AbstractDelayedSaveCache<FullDataSourceV2, DelayedDataSourceSaveCache.DataSourceSaveObjContainer> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().name(DelayedDataSourceSaveCache.class.getSimpleName()).build();
   private final DelayedDataSourceSaveCache.ISaveDataSourceFunc onSaveTimeoutAsyncFunc;

   public DelayedDataSourceSaveCache(@NotNull DelayedDataSourceSaveCache.ISaveDataSourceFunc onSaveTimeoutAsyncFunc, int saveDelayInMs) {
      super(saveDelayInMs);
      this.onSaveTimeoutAsyncFunc = onSaveTimeoutAsyncFunc;
   }

   public void writeToMemoryAndQueueSave(@NotNull FullDataSourceV2 inputObj) {
      super.writeToMemoryAndQueueSave(inputObj.getPos(), inputObj);
   }

   protected DelayedDataSourceSaveCache.DataSourceSaveObjContainer createEmptySaveObjContainer(long inputPos) {
      return new DelayedDataSourceSaveCache.DataSourceSaveObjContainer(inputPos);
   }

   protected void handleDataSourceRemoval(@NotNull DelayedDataSourceSaveCache.DataSourceSaveObjContainer saveContainer) {
      FullDataSourceV2 removedDataSource = saveContainer.dataSource;
      this.onSaveTimeoutAsyncFunc.saveAsync(removedDataSource).handle((voidObj, throwable) -> {
         try {
            removedDataSource.close();
         } catch (Exception var4) {
            LOGGER.error("Unable to close datasource [" + DhSectionPos.toString(removedDataSource.getPos()) + "], error: [" + var4.getMessage() + "].", var4);
         }

         return null;
      });
   }

   public static class DataSourceSaveObjContainer extends AbstractSaveObjContainer<FullDataSourceV2> {
      @NotNull
      private final FullDataSourceV2 dataSource;

      public DataSourceSaveObjContainer(long inputPos) {
         this.dataSource = FullDataSourceV2.createEmpty(inputPos);
      }

      public void update(@Nullable FullDataSourceV2 newObj) {
         if (newObj == null) {
            throw new NullPointerException();
         } else {
            this.dataSource.updateFromDataSource(newObj);
         }
      }
   }

   @FunctionalInterface
   public interface ISaveDataSourceFunc {
      CompletableFuture<Void> saveAsync(@NotNull FullDataSourceV2 fullDataSourceV2);
   }
}
