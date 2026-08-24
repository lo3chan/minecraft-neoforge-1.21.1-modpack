package com.seibel.distanthorizons.api.interfaces.data;

public interface IDhApiTerrainDataCache extends AutoCloseable {
   void clear();

   @Override
   void close();
}
