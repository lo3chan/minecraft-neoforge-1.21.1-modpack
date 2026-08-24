package com.seibel.distanthorizons.core.wrapperInterfaces.minecraft;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.ILightMapWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMinecraftRenderWrapper extends IBindable {
   DhVec3f getLookAtVector();

   boolean playerHasBlindingEffect();

   float getPartialTickTime();

   DhVec3d getCameraExactPosition();

   Color getFogColor(float f);

   boolean isFogStateSpecial();

   Color getSkyColor();

   int getRenderDistance();

   double getFovSetting();

   int getFrameLimit();

   boolean mcRendersToFrameBuffer();

   boolean runningLegacyOpenGL();

   EDhApiRenderingApi getMcRenderingApi();

   int getTargetFramebuffer();

   int getGlDepthTextureId();

   int getGlColorTextureId();

   int getTargetFramebufferViewportWidth();

   int getTargetFramebufferViewportHeight();

   void clearTargetFrameBuffer();

   @Nullable
   ILightMapWrapper getLightmapWrapper(@NotNull ILevelWrapper iLevelWrapper);
}
