package com.seibel.distanthorizons.api.interfaces.block;

import com.seibel.distanthorizons.api.interfaces.IDhApiUnsafeWrapper;

public interface IDhApiBlockStateWrapper extends IDhApiUnsafeWrapper {
   boolean isAir();

   boolean isSolid();

   boolean isLiquid();

   int getOpacity();

   String getSerialString();

   byte getMaterialId();
}
