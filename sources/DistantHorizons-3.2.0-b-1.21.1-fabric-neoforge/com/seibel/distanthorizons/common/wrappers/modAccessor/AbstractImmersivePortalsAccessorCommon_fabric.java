package com.seibel.distanthorizons.common.wrappers.modAccessor;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.AbstractImmersivePortalsAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import net.minecraft.class_638;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractImmersivePortalsAccessorCommon_fabric extends AbstractImmersivePortalsAccessor {
   @Nullable
   public static volatile class_638 actualLevel;
   @Nullable
   public static volatile DhBlockPos actualBlockPos;
   @Nullable
   public static volatile DhChunkPos actualChunkPos;
   @Nullable
   public static volatile DhVec3d actualCameraPos;

   @Nullable
   @Override
   public DhBlockPos getActualPlayerBlockPos() {
      return actualBlockPos;
   }

   @Nullable
   @Override
   public DhChunkPos getActualPlayerChunkPos() {
      return actualChunkPos;
   }

   @Nullable
   @Override
   public IClientLevelWrapper getActualClientLevelWrapper() {
      return ClientLevelWrapper_fabric.getWrapper(actualLevel, false);
   }

   @Nullable
   @Override
   public DhVec3d getActualCameraPos() {
      return actualCameraPos;
   }
}
