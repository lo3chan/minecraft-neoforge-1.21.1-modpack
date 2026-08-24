package com.seibel.distanthorizons.api.objects.data;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;

public class DhApiTerrainDataPoint {
   public final byte detailLevel;
   public final int blockLightLevel;
   public final int skyLightLevel;
   public final int bottomYBlockPos;
   public final int topYBlockPos;
   public final IDhApiBlockStateWrapper blockStateWrapper;
   public final IDhApiBiomeWrapper biomeWrapper;

   public static DhApiTerrainDataPoint create(
      byte detailLevel,
      int blockLightLevel,
      int skyLightLevel,
      int bottomYBlockPos,
      int topYBlockPos,
      IDhApiBlockStateWrapper blockStateWrapper,
      IDhApiBiomeWrapper biomeWrapper
   ) {
      return new DhApiTerrainDataPoint(detailLevel, blockLightLevel, skyLightLevel, bottomYBlockPos, topYBlockPos, blockStateWrapper, biomeWrapper);
   }

   private DhApiTerrainDataPoint(
      byte detailLevel,
      int blockLightLevel,
      int skyLightLevel,
      int bottomYBlockPos,
      int topYBlockPos,
      IDhApiBlockStateWrapper blockStateWrapper,
      IDhApiBiomeWrapper biomeWrapper
   ) {
      this.detailLevel = detailLevel;
      this.blockLightLevel = blockLightLevel;
      this.skyLightLevel = skyLightLevel;
      this.bottomYBlockPos = bottomYBlockPos;
      this.topYBlockPos = topYBlockPos;
      this.blockStateWrapper = blockStateWrapper;
      this.biomeWrapper = biomeWrapper;
   }

   @Override
   public String toString() {
      return "[Block:"
         + this.blockStateWrapper.getSerialString()
         + ",Biome:"
         + this.biomeWrapper.getName()
         + ",TopY:"
         + this.topYBlockPos
         + ",BottomY:"
         + this.bottomYBlockPos
         + ",BlockLight:"
         + this.blockLightLevel
         + ",SkyLight:"
         + this.skyLightLevel
         + "]";
   }
}
