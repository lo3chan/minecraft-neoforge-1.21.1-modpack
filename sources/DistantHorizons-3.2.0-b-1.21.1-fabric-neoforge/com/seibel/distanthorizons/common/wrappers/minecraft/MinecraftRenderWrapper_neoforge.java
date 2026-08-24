package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.common.wrappers.misc.LightMapWrapper_neoforge;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.ILightMapWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IOptifineAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IDimensionTypeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.awt.Color;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinecraftRenderWrapper_neoforge implements IMinecraftRenderWrapper {
   public static final MinecraftRenderWrapper_neoforge INSTANCE = new MinecraftRenderWrapper_neoforge();
   private static final IOptifineAccessor OPTIFINE_ACCESSOR = ModAccessorInjector.INSTANCE.get(IOptifineAccessor.class);
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Minecraft MC = Minecraft.getInstance();
   public ConcurrentHashMap<IDimensionTypeWrapper, LightMapWrapper_neoforge> lightmapByDimensionType = new ConcurrentHashMap<>();
   public int finalLevelFrameBufferId = -1;
   public boolean colorTextureCastFailLogged = false;
   public boolean depthTextureCastFailLogged = false;
   private EDhApiRenderingApi renderApi = null;

   @Override
   public DhVec3f getLookAtVector() {
      Camera camera = MC.gameRenderer.getMainCamera();
      return new DhVec3f(camera.getLookVector().x(), camera.getLookVector().y(), camera.getLookVector().z());
   }

   @Override
   public boolean playerHasBlindingEffect() {
      if (MC.player == null) {
         return false;
      } else {
         return MC.player.getActiveEffectsMap() == null
            ? false
            : MC.player.getActiveEffectsMap().get(MobEffects.BLINDNESS) != null || MC.player.getActiveEffectsMap().get(MobEffects.DARKNESS) != null;
      }
   }

   @Override
   public DhVec3d getCameraExactPosition() {
      if (MinecraftRenderWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS != null && !RenderThreadTaskHandler.INSTANCE.isCurrentThread()) {
         DhVec3d cameraPos = MinecraftRenderWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS.getActualCameraPos();
         if (cameraPos != null) {
            return cameraPos;
         }
      }

      Camera camera = MC.gameRenderer.getMainCamera();
      Vec3 projectedView = camera.getPosition();
      return new DhVec3d(projectedView.x, projectedView.y, projectedView.z);
   }

   @Override
   public float getPartialTickTime() {
      return MC.getTimer().getRealtimeDeltaTicks();
   }

   @Override
   public Color getFogColor(float partialTicks) {
      FogRenderer.setupColor(MC.gameRenderer.getMainCamera(), partialTicks, MC.level, 1, MC.gameRenderer.getDarkenWorldAmount(partialTicks));
      float[] colorValues = RenderSystem.getShaderFogColor();
      return new Color(
         Math.max(0.0F, Math.min(colorValues[0], 1.0F)),
         Math.max(0.0F, Math.min(colorValues[1], 1.0F)),
         Math.max(0.0F, Math.min(colorValues[2], 1.0F)),
         Math.max(0.0F, Math.min(colorValues[3], 1.0F))
      );
   }

   @Override
   public Color getSkyColor() {
      if (MC.level.dimensionType().hasSkyLight()) {
         float frameTime = this.getPartialTickTime();
         Vec3 colorValues = MC.level.getSkyColor(MC.gameRenderer.getMainCamera().getPosition(), frameTime);
         return new Color((float)colorValues.x, (float)colorValues.y, (float)colorValues.z);
      } else {
         return new Color(0, 0, 0);
      }
   }

   @Override
   public int getRenderDistance() {
      return MC.options.getEffectiveRenderDistance();
   }

   @Override
   public double getFovSetting() {
      return ((Integer)MC.options.fov().get()).intValue();
   }

   @Override
   public int getFrameLimit() {
      return (Integer)MC.options.framerateLimit().get();
   }

   public RenderTarget getRenderTarget() {
      return MC.getMainRenderTarget();
   }

   @Override
   public boolean mcRendersToFrameBuffer() {
      return true;
   }

   @Override
   public boolean runningLegacyOpenGL() {
      return false;
   }

   @Override
   public EDhApiRenderingApi getMcRenderingApi() {
      if (this.renderApi != null) {
         return this.renderApi;
      } else {
         this.renderApi = EDhApiRenderingApi.OPEN_GL;
         return this.renderApi;
      }
   }

   @Override
   public int getTargetFramebuffer() {
      return OPTIFINE_ACCESSOR != null ? this.finalLevelFrameBufferId : this.getRenderTarget().frameBufferId;
   }

   @Override
   public void clearTargetFrameBuffer() {
      this.finalLevelFrameBufferId = -1;
   }

   @Override
   public int getGlDepthTextureId() {
      return this.getRenderTarget().getDepthTextureId();
   }

   @Override
   public int getGlColorTextureId() {
      return this.getRenderTarget().getColorTextureId();
   }

   @Override
   public int getTargetFramebufferViewportWidth() {
      return this.getRenderTarget().viewWidth;
   }

   @Override
   public int getTargetFramebufferViewportHeight() {
      return this.getRenderTarget().viewHeight;
   }

   @Override
   public boolean isFogStateSpecial() {
      boolean isBlind = this.playerHasBlindingEffect();
      return MC.gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE || isBlind;
   }

   @Override
   public ILightMapWrapper getLightmapWrapper(@NotNull ILevelWrapper level) {
      return this.lightmapByDimensionType.get(level.getDimensionType());
   }

   public void updateLightmap(NativeImage lightPixels) {
      IClientLevelWrapper clientLevel = getLightmapClientLevelWrapper();
      if (clientLevel != null) {
         IDimensionTypeWrapper dimensionType = clientLevel.getDimensionType();
         LightMapWrapper_neoforge wrapper = this.lightmapByDimensionType.computeIfAbsent(dimensionType, dimType -> new LightMapWrapper_neoforge());
         wrapper.uploadLightmap(lightPixels);
      }
   }

   public void setLightmapId(int textureId) {
      IClientLevelWrapper clientLevel = getLightmapClientLevelWrapper();
      if (clientLevel != null) {
         IDimensionTypeWrapper dimensionType = clientLevel.getDimensionType();
         LightMapWrapper_neoforge wrapper = this.lightmapByDimensionType.computeIfAbsent(dimensionType, dimType -> new LightMapWrapper_neoforge());
         wrapper.setLightmapId(textureId);
      }
   }

   @Nullable
   private static IClientLevelWrapper getLightmapClientLevelWrapper() {
      IClientLevelWrapper clientLevel = ClientApi.RENDER_STATE.clientLevelWrapper;
      if (clientLevel == null) {
         clientLevel = MC_CLIENT.getWrappedClientLevel();
      }

      return clientLevel;
   }
}
