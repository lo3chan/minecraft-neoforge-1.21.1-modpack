package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.modAccessor.AbstractImmersivePortalsAccessorCommon_fabric;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;

public class MixinImmersivePortalsRenderStatesCommon_fabric {
   public static void saveVolatileOriginals() {
      class_310 mc = class_310.method_1551();
      AbstractImmersivePortalsAccessorCommon_fabric.actualLevel = mc.field_1687;
      if (mc.field_1724 == null) {
         AbstractImmersivePortalsAccessorCommon_fabric.actualBlockPos = null;
         AbstractImmersivePortalsAccessorCommon_fabric.actualChunkPos = null;
         AbstractImmersivePortalsAccessorCommon_fabric.actualCameraPos = null;
      } else {
         class_2338 playerBlockPos = mc.field_1724.method_24515();
         AbstractImmersivePortalsAccessorCommon_fabric.actualBlockPos = new DhBlockPos(
            playerBlockPos.method_10263(), playerBlockPos.method_10264(), playerBlockPos.method_10260()
         );
         class_1923 playerChunkPos = mc.field_1724.method_31476();
         AbstractImmersivePortalsAccessorCommon_fabric.actualChunkPos = McObjectConverter_fabric.convert(playerChunkPos);
         class_243 cameraPos = mc.field_1773.method_19418().method_19326();
         AbstractImmersivePortalsAccessorCommon_fabric.actualCameraPos = new DhVec3d(
            cameraPos.method_10216(), cameraPos.method_10214(), cameraPos.method_10215()
         );
      }
   }
}
