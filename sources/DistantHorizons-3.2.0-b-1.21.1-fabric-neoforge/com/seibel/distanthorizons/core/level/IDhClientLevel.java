package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import org.jetbrains.annotations.Nullable;

public interface IDhClientLevel extends IDhLevel {
   void clientTick();

   @Nullable
   IClientLevelWrapper getClientLevelWrapper();

   void clearRenderCache();

   boolean isRendering();
}
