package com.seibel.distanthorizons.api.interfaces.world;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiLevelType;
import com.seibel.distanthorizons.api.interfaces.IDhApiUnsafeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderRegister;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import java.awt.Color;
import java.io.File;

public interface IDhApiLevelWrapper extends IDhApiUnsafeWrapper {
   IDhApiDimensionTypeWrapper getDimensionType();

   String getDimensionName();

   String getDhIdentifier();

   EDhApiLevelType getLevelType();

   boolean hasCeiling();

   boolean hasSkyLight();

   @Deprecated
   default int getHeight() {
      return this.getMaxHeight();
   }

   int getMaxHeight();

   default int getMinHeight() {
      return 0;
   }

   IDhApiCustomRenderRegister getRenderRegister();

   File getDhSaveFolder();

   DhApiResult<Color> getBlockColorPreApi(
      IDhApiBlockStateWrapper iDhApiBlockStateWrapper, IDhApiBiomeWrapper iDhApiBiomeWrapper, int i, int j, int k, IDhApiFullDataSource iDhApiFullDataSource
   );
}
