package com.seibel.distanthorizons.core.generation.tasks;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import org.jetbrains.annotations.Nullable;

public class DataSourceRetrievalResult {
   public final ERetrievalResultState state;
   public final long pos;
   @Nullable
   public final FullDataSourceV2 dataSource;

   public static DataSourceRetrievalResult CreateSplit() {
      return new DataSourceRetrievalResult(ERetrievalResultState.REQUIRES_SPLITTING, 0L, null);
   }

   public static DataSourceRetrievalResult CreateSuccess(long pos, FullDataSourceV2 generatedDataSource) {
      return new DataSourceRetrievalResult(ERetrievalResultState.SUCCESS, pos, generatedDataSource);
   }

   private DataSourceRetrievalResult(ERetrievalResultState state, long pos, @Nullable FullDataSourceV2 dataSource) {
      this.state = state;
      this.pos = pos;
      this.dataSource = dataSource;
   }
}
