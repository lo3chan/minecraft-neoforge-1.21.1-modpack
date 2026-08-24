package com.seibel.distanthorizons.api.interfaces.world;

import com.seibel.distanthorizons.api.interfaces.IDhApiUnsafeWrapper;

public interface IDhApiDimensionTypeWrapper extends IDhApiUnsafeWrapper {
   boolean hasCeiling();

   boolean hasSkyLight();
}
