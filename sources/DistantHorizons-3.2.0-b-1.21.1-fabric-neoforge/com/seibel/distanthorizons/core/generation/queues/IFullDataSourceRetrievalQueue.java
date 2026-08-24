package com.seibel.distanthorizons.core.generation.queues;

import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IFullDataSourceRetrievalQueue extends Closeable {
   byte lowestDataDetail();

   byte highestDataDetail();

   String getRetrievalTypeName();

   void startAndSetTargetPos(DhBlockPos2D dhBlockPos2D);

   void removeRetrievalRequestIf(DhSectionPos.ICancelablePrimitiveLongConsumer iCancelablePrimitiveLongConsumer);

   CompletableFuture<DataSourceRetrievalResult> submitRetrievalTask(long l, byte b);

   CompletableFuture<Void> startClosingAsync(boolean bl, boolean bl2);

   @Override
   void close();

   int getWaitingTaskCount();

   int getInProgressTaskCount();

   int getQueuedChunkCount();

   int getEstimatedRemainingTaskCount();

   void setEstimatedRemainingTaskCount(int i);

   int getRetrievalEstimatedRemainingChunkCount();

   void setRetrievalEstimatedRemainingChunkCount(int i);

   void addDebugMenuStringsToList(List<String> list);

   RollingAverage getRollingAverageChunkGenTimeInMs();
}
