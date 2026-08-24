package com.seibel.distanthorizons.api.objects.data;

import java.util.List;

public interface IDhApiFullDataSource {
   int getWidthInDataColumns();

   List<DhApiTerrainDataPoint> setApiDataPointColumn(int i, int j, List<DhApiTerrainDataPoint> list) throws IndexOutOfBoundsException, IllegalArgumentException;

   List<DhApiTerrainDataPoint> getApiDataPointColumn(int i, int j) throws IndexOutOfBoundsException;
}
