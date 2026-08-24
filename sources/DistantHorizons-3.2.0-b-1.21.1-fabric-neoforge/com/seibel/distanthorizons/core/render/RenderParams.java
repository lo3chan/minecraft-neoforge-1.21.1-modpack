package com.seibel.distanthorizons.core.render;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiRenderPass;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.api.internal.rendering.DhRenderState;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.world.IDhClientWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.ILightMapWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IOptifineAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;

public class RenderParams extends DhApiRenderParam {
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IOptifineAccessor OPTIFINE_ACCESSOR = ModAccessorInjector.INSTANCE.get(IOptifineAccessor.class);
   public final DhApiRenderParam apiCopy = new DhApiRenderParam();
   public IDhClientWorld dhClientWorld;
   public IDhClientLevel dhClientLevel;
   public IClientLevelWrapper clientLevelWrapper;
   public ILightMapWrapper lightmap;
   public RenderBufferHandler renderBufferHandler;
   public IDhGenericRenderer genericRenderer;
   public DhVec3d exactCameraPosition;
   public boolean vanillaFogEnabled;
   public boolean hasBeenValidated = false;

   public void update(EDhApiRenderPass renderPass, DhRenderState renderState) {
      RenderUtil.setDhProjectionMatrix(this.dhProjectionMatrix, renderState.mcProjectionMatrix);
      this.dhModelViewMatrix.set(renderState.mcModelViewMatrix);
      super.update(
         renderPass,
         renderState.partialTickTime,
         RenderUtil.getNearClipPlaneInBlocks(),
         RenderUtil.getFarClipPlaneDistanceInBlocks(),
         renderState.mcProjectionMatrix,
         renderState.mcModelViewMatrix,
         this.dhProjectionMatrix,
         this.dhModelViewMatrix,
         renderState.clientLevelWrapper.getMinHeight(),
         renderState.clientLevelWrapper
      );
      this.clientLevelWrapper = renderState.clientLevelWrapper;
      this.dhClientWorld = SharedApi.tryGetDhClientWorld();
      if (this.dhClientWorld != null) {
         this.dhClientLevel = this.dhClientWorld.getOrLoadClientLevel(this.clientLevelWrapper);
         if (this.dhClientLevel != null) {
            this.renderBufferHandler = this.dhClientLevel.getRenderBufferHandler();
            this.genericRenderer = this.dhClientLevel.getGenericRenderer();
         }
      }

      this.lightmap = MC_RENDER.getLightmapWrapper(this.clientLevelWrapper);
      if (MC_CLIENT.playerExists()) {
         this.exactCameraPosition = MC_RENDER.getCameraExactPosition();
      }

      this.vanillaFogEnabled = renderState.vanillaFogEnabled;
      this.apiCopy.update(this);
   }

   public String getValidationErrorMessage() {
      this.hasBeenValidated = true;
      if (!MC_CLIENT.playerExists()) {
         return "No Player Exists";
      } else if (this.dhClientWorld == null) {
         return "No DH Client World Loaded";
      } else if (this.dhClientLevel == null) {
         return "No DH Client Level Loaded";
      } else if (this.clientLevelWrapper == null) {
         return "No Client Level Wrapper Loaded";
      } else if (this.lightmap == null) {
         return "No Lightmap Loaded";
      } else if (this.renderBufferHandler == null) {
         return "No RenderBufferHandler Present";
      } else if (this.genericRenderer == null) {
         return "No Generic Renderer Present";
      } else if (this.dhModelViewMatrix.equals(DhMat4f.IDENTITY) || this.dhModelViewMatrix.equals(DhMat4f.EMPTY)) {
         return "No DH MVM Matrix Given";
      } else if (!this.mcModelViewMatrix.equals(DhMat4f.IDENTITY) && !this.mcModelViewMatrix.equals(DhMat4f.EMPTY)) {
         return OPTIFINE_ACCESSOR != null && MC_RENDER.getTargetFramebuffer() == -1 ? "Optifine Target Frame Buffer not set" : null;
      } else {
         return "No MC MVM Matrix Given";
      }
   }
}
