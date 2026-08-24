package com.seibel.distanthorizons.core.wrapperInterfaces.block;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import java.awt.Color;

public interface IBlockStateWrapper extends IDhApiBlockStateWrapper {
   @Override
   String getSerialString();

   @Override
   int getOpacity();

   int getLightEmission();

   @Override
   byte getMaterialId();

   boolean isBeaconBlock();

   boolean isBeaconTintBlock();

   boolean allowsBeaconBeamPassage();

   boolean isBeaconBaseBlock();

   boolean renderTexture();

   boolean useBottomTextureForSides();

   boolean alwaysRasterizeTexture();

   boolean allowApiColorOverride();

   Color getMapColor();

   Color getBeaconTintColor();
}
