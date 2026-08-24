package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.modAccessor.AbstractImmersivePortalsAccessorCommon_neoforge;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class MixinImmersivePortalsRenderStatesCommon_neoforge {
   public static void saveVolatileOriginals() {
      Minecraft mc = Minecraft.getInstance();
      AbstractImmersivePortalsAccessorCommon_neoforge.actualLevel = mc.level;
      if (mc.player == null) {
         AbstractImmersivePortalsAccessorCommon_neoforge.actualBlockPos = null;
         AbstractImmersivePortalsAccessorCommon_neoforge.actualChunkPos = null;
         AbstractImmersivePortalsAccessorCommon_neoforge.actualCameraPos = null;
      } else {
         BlockPos playerBlockPos = mc.player.blockPosition();
         AbstractImmersivePortalsAccessorCommon_neoforge.actualBlockPos = new DhBlockPos(playerBlockPos.getX(), playerBlockPos.getY(), playerBlockPos.getZ());
         ChunkPos playerChunkPos = mc.player.chunkPosition();
         AbstractImmersivePortalsAccessorCommon_neoforge.actualChunkPos = McObjectConverter_neoforge.convert(playerChunkPos);
         Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
         AbstractImmersivePortalsAccessorCommon_neoforge.actualCameraPos = new DhVec3d(cameraPos.x(), cameraPos.y(), cameraPos.z());
      }
   }
}
