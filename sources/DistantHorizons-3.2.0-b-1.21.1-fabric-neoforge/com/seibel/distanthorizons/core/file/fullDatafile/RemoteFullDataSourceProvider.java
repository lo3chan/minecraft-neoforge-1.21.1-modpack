package com.seibel.distanthorizons.core.file.fullDatafile;

import com.google.common.cache.CacheBuilder;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.generation.tasks.ERetrievalResultState;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.multiplayer.client.SyncOnLoadRequestQueue;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

public class RemoteFullDataSourceProvider extends GeneratedFullDataSourceProvider {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Nullable
   private final SyncOnLoadRequestQueue syncOnLoadRequestQueue;
   private final Set<Long> visitedPositions = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(20L, TimeUnit.MINUTES).build().asMap());

   public RemoteFullDataSourceProvider(
      IDhLevel level, ISaveStructure saveStructure, @Nullable File saveDirOverride, @Nullable SyncOnLoadRequestQueue syncOnLoadRequestQueue
   ) throws SQLException, IOException {
      super(level, saveStructure, saveDirOverride);
      this.syncOnLoadRequestQueue = syncOnLoadRequestQueue;
   }

   @Override
   public boolean canQueueRetrievalNow() {
      return this.canQueueRetrievalNow(true);
   }

   @Nullable
   @Override
   public FullDataSourceV2 get(long pos) {
      if (this.syncOnLoadRequestQueue == null) {
         return super.get(pos);
      } else if (!this.visitedPositions.add(pos)) {
         return super.get(pos);
      } else {
         Long timestamp = this.getTimestampForPos(pos);
         if (timestamp != null) {
            this.syncOnLoadRequestQueue.submitRequest(pos, timestamp).thenAccept(result -> {
               if (result.state == ERetrievalResultState.SUCCESS && result.dataSource != null) {
                  this.updateDataSourceAsync(result.dataSource).handle((voidObj, throwable) -> {
                     result.dataSource.close();
                     return null;
                  });
               }
            });
         }

         return super.get(pos);
      }
   }

   @Override
   public void close() {
      if (this.syncOnLoadRequestQueue != null) {
         this.syncOnLoadRequestQueue.close();
      }

      super.close();
   }
}
