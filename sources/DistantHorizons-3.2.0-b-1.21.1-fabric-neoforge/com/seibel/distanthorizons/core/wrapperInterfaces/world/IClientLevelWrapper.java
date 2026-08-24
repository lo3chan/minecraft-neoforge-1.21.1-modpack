package com.seibel.distanthorizons.core.wrapperInterfaces.world;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import java.awt.Color;
import org.jetbrains.annotations.Nullable;

public interface IClientLevelWrapper extends ILevelWrapper {
   void markAccessed();

   @Nullable
   IServerLevelWrapper tryGetServerSideWrapper();

   default int getBlockColor(DhBlockPos pos, IBiomeWrapper biome, FullDataSourceV2 fullDataSource, IBlockStateWrapper blockState) {
      return this.getBlockColor(pos, biome, fullDataSource, blockState, true);
   }

   int getBlockColor(DhBlockPos dhBlockPos, IBiomeWrapper iBiomeWrapper, FullDataSourceV2 fullDataSourceV2, IBlockStateWrapper iBlockStateWrapper, boolean bl);

   int getDirtBlockColor();

   void clearBlockColorCache();

   Color getCloudColor(float f);

   float getShade(EDhDirection eDhDirection);
}
