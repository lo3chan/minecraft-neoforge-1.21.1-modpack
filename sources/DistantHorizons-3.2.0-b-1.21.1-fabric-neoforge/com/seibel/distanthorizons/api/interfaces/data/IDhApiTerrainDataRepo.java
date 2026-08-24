package com.seibel.distanthorizons.api.interfaces.data;

import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.data.DhApiRaycastResult;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;

public interface IDhApiTerrainDataRepo {
   DhApiResult<DhApiTerrainDataPoint> getSingleDataPointAtBlockPos(
      IDhApiLevelWrapper iDhApiLevelWrapper, int i, int j, int k, IDhApiTerrainDataCache iDhApiTerrainDataCache
   );

   DhApiResult<DhApiTerrainDataPoint[]> getColumnDataAtBlockPos(
      IDhApiLevelWrapper iDhApiLevelWrapper, int i, int j, IDhApiTerrainDataCache iDhApiTerrainDataCache
   );

   DhApiResult<DhApiTerrainDataPoint[][][]> getAllTerrainDataAtChunkPos(
      IDhApiLevelWrapper iDhApiLevelWrapper, int i, int j, IDhApiTerrainDataCache iDhApiTerrainDataCache
   );

   DhApiResult<DhApiTerrainDataPoint[][][]> getAllTerrainDataAtRegionPos(
      IDhApiLevelWrapper iDhApiLevelWrapper, int i, int j, IDhApiTerrainDataCache iDhApiTerrainDataCache
   );

   DhApiResult<DhApiTerrainDataPoint[][][]> getAllTerrainDataAtDetailLevelAndPos(
      IDhApiLevelWrapper iDhApiLevelWrapper, byte b, int i, int j, IDhApiTerrainDataCache iDhApiTerrainDataCache
   );

   DhApiResult<DhApiRaycastResult> raycast(
      IDhApiLevelWrapper iDhApiLevelWrapper, double d, double e, double f, float g, float h, float i, int j, IDhApiTerrainDataCache iDhApiTerrainDataCache
   );

   DhApiResult<Void> overwriteChunkDataAsync(IDhApiLevelWrapper iDhApiLevelWrapper, Object[] objects) throws ClassCastException;

   IDhApiTerrainDataCache createSoftCache();
}
