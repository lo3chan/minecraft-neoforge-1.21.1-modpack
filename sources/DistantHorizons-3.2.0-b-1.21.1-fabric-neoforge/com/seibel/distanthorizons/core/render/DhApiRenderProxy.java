package com.seibel.distanthorizons.core.render;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderProxy;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import org.jetbrains.annotations.Nullable;

public class DhApiRenderProxy implements IDhApiRenderProxy {
   public static final DhApiRenderProxy INSTANCE = new DhApiRenderProxy();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private boolean deferTransparentRendering = false;
   private static AbstractDhRenderApiDefinition renderApiDef = null;
   public static int activeOpenGlDhDepthTextureId = -1;
   public static int activeOpenGlDhColorTextureId = -1;

   @Nullable
   private static AbstractDhRenderApiDefinition tryGetApiDef() {
      if (renderApiDef == null) {
         renderApiDef = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
      }

      return renderApiDef;
   }

   private DhApiRenderProxy() {
   }

   @Override
   public DhApiResult<Boolean> clearRenderDataCache() {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world == null) {
         return DhApiResult.createFail("No world loaded");
      } else {
         for (IDhLevel level : world.getAllLoadedLevels()) {
            if (level instanceof IDhClientLevel) {
               ((IDhClientLevel)level).clearRenderCache();
            }
         }

         return DhApiResult.createSuccess();
      }
   }

   @Override
   public EDhApiRenderingApi getRenderingApi() throws IllegalStateException {
      AbstractDhRenderApiDefinition apiDef = tryGetApiDef();
      if (apiDef == null) {
         throw new IllegalStateException("Distant Horizons hasn't finished setup yet. No renderer has been set.");
      } else {
         return apiDef.getRenderApi();
      }
   }

   @Override
   public boolean isNativeRenderer() throws IllegalStateException {
      AbstractDhRenderApiDefinition apiDef = tryGetApiDef();
      if (apiDef == null) {
         throw new IllegalStateException("Distant Horizons hasn't finished setup yet. No renderer has been set.");
      } else {
         return apiDef.isNativeRenderer();
      }
   }

   @Override
   public DhApiResult<Integer> getDhDepthTextureId() {
      int activeTexture = activeOpenGlDhDepthTextureId;
      return activeTexture == -1
         ? DhApiResult.createFail("DH's depth texture hasn't been created and/or bound yet.", -1)
         : DhApiResult.createSuccess(activeTexture);
   }

   @Override
   public DhApiResult<Integer> getDhColorTextureId() {
      int activeTexture = activeOpenGlDhColorTextureId;
      return activeTexture == -1
         ? DhApiResult.createFail("DH's color texture hasn't been created and/or bound yet.", -1)
         : DhApiResult.createSuccess(activeTexture);
   }

   @Override
   public void setDeferTransparentRendering(boolean deferTransparentRendering) {
      this.deferTransparentRendering = deferTransparentRendering;
   }

   @Override
   public boolean getDeferTransparentRendering() {
      return this.deferTransparentRendering;
   }

   @Override
   public float getNearClipPlaneDistanceInBlocks(float partialTicks) {
      return RenderUtil.getNearClipPlaneInBlocks();
   }
}
