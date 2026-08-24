package com.seibel.distanthorizons.core.api.external.methods.data;

import com.seibel.distanthorizons.api.interfaces.data.IDhApiTerrainDataCache;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.ref.SoftReference;
import org.jetbrains.annotations.Nullable;

public class DhApiTerrainDataCache implements IDhApiTerrainDataCache {
   private final Object modificationLock = new Object();
   private final Long2ReferenceOpenHashMap<SoftReference<FullDataSourceV2>> posToFullDataRef = new Long2ReferenceOpenHashMap();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public void add(long pos, FullDataSourceV2 dataSource) {
      synchronized (this.modificationLock) {
         this.posToFullDataRef.put(pos, new SoftReference<>(dataSource));
      }
   }

   @Nullable
   public FullDataSourceV2 get(long pos) {
      synchronized (this.modificationLock) {
         SoftReference<FullDataSourceV2> ref = (SoftReference<FullDataSourceV2>)this.posToFullDataRef.get(pos);
         return ref != null ? ref.get() : null;
      }
   }

   @Override
   public void clear() {
      synchronized (this.modificationLock) {
         LongSet keySet = this.posToFullDataRef.keySet();
         LongIterator var3 = keySet.iterator();

         while (var3.hasNext()) {
            long pos = (Long)var3.next();
            SoftReference<FullDataSourceV2> dataRef = (SoftReference<FullDataSourceV2>)this.posToFullDataRef.remove(pos);
            if (dataRef != null) {
               FullDataSourceV2 dataSource = dataRef.get();
               if (dataSource != null) {
                  try {
                     dataSource.close();
                  } catch (Exception var10) {
                     LOGGER.warn("Unable to close data source, error: [" + var10.getMessage() + "].", var10);
                  }
               }
            }
         }
      }
   }

   @Override
   public void close() {
      this.clear();
   }

   @Override
   public String toString() {
      return "Size: " + this.posToFullDataRef.size();
   }
}
