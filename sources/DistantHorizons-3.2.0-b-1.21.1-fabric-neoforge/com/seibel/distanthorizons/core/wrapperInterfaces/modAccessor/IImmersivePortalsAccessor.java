package com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor;

import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import org.jetbrains.annotations.Nullable;

public interface IImmersivePortalsAccessor extends IModAccessor {
   String CORE_MOD_ID = "imm_ptl_core";
   String MOD_ID = "immersive_portals";
   String INJECTION_CLASS_1_16 = "com.qouteall.immersive_portals.render.context_management.RenderStates";
   String INJECTION_CLASS = "qouteall.imm_ptl.core.render.context_management.RenderStates";

   boolean isRenderingPortal();

   @Nullable
   DhBlockPos getActualPlayerBlockPos();

   @Nullable
   DhChunkPos getActualPlayerChunkPos();

   @Nullable
   IClientLevelWrapper getActualClientLevelWrapper();

   @Nullable
   DhVec3d getActualCameraPos();
}
