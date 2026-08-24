package com.seibel.distanthorizons.core.generation.tasks;

import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import java.util.concurrent.CompletableFuture;

public final class DataSourceRetrievalTask {
   public final long pos;
   public final byte requestDetailLevel;
   public final int widthInChunks;
   public final CompletableFuture<DataSourceRetrievalResult> future = new CompletableFuture<>();

   public DataSourceRetrievalTask(long pos, byte dataDetail) {
      this.pos = pos;
      this.requestDetailLevel = dataDetail;
      this.widthInChunks = BitShiftUtil.powerOfTwo(DhSectionPos.getDetailLevel(this.pos) - this.requestDetailLevel - 4);
   }
}
