package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.common.wrappers.misc.LightMapWrapper_fabric;
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
import net.minecraft.class_1011;
import net.minecraft.class_1294;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import net.minecraft.class_758;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinecraftRenderWrapper_fabric implements IMinecraftRenderWrapper {
   public static final MinecraftRenderWrapper_fabric INSTANCE = new MinecraftRenderWrapper_fabric();
   private static final IOptifineAccessor OPTIFINE_ACCESSOR = ModAccessorInjector.INSTANCE.get(IOptifineAccessor.class);
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final class_310 MC = class_310.method_1551();
   public ConcurrentHashMap<IDimensionTypeWrapper, LightMapWrapper_fabric> lightmapByDimensionType = new ConcurrentHashMap<>();
   public int finalLevelFrameBufferId = -1;
   public boolean colorTextureCastFailLogged = false;
   public boolean depthTextureCastFailLogged = false;
   private EDhApiRenderingApi renderApi = null;

   @Override
   public DhVec3f getLookAtVector() {
      class_4184 camera = MC.field_1773.method_19418();
      return new DhVec3f(camera.method_19335().x(), camera.method_19335().y(), camera.method_19335().z());
   }

   @Override
   public boolean playerHasBlindingEffect() {
      if (MC.field_1724 == null) {
         return false;
      } else {
         return MC.field_1724.method_6088() == null
            ? false
            : MC.field_1724.method_6088().get(class_1294.field_5919) != null || MC.field_1724.method_6088().get(class_1294.field_38092) != null;
      }
   }

   @Override
   public DhVec3d getCameraExactPosition() {
      if (MinecraftRenderWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS != null && !RenderThreadTaskHandler.INSTANCE.isCurrentThread()) {
         DhVec3d cameraPos = MinecraftRenderWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS.getActualCameraPos();
         if (cameraPos != null) {
            return cameraPos;
         }
      }

      class_4184 camera = MC.field_1773.method_19418();
      class_243 projectedView = camera.method_19326();
      return new DhVec3d(projectedView.field_1352, projectedView.field_1351, projectedView.field_1350);
   }

   @Override
   public float getPartialTickTime() {
      return MC.method_60646().method_60638();
   }

   @Override
   public Color getFogColor(float partialTicks) {
      class_758.method_3210(MC.field_1773.method_19418(), partialTicks, MC.field_1687, 1, MC.field_1773.method_3195(partialTicks));
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
      if (MC.field_1687.method_8597().comp_642()) {
         float frameTime = this.getPartialTickTime();
         class_243 colorValues = MC.field_1687.method_23777(MC.field_1773.method_19418().method_19326(), frameTime);
         return new Color((float)colorValues.field_1352, (float)colorValues.field_1351, (float)colorValues.field_1350);
      } else {
         return new Color(0, 0, 0);
      }
   }

   @Override
   public int getRenderDistance() {
      return MC.field_1690.method_38521();
   }

   @Override
   public double getFovSetting() {
      return ((Integer)MC.field_1690.method_41808().method_41753()).intValue();
   }

   @Override
   public int getFrameLimit() {
      return (Integer)MC.field_1690.method_42524().method_41753();
   }

   public class_276 getRenderTarget() {
      return MC.method_1522();
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
      return OPTIFINE_ACCESSOR != null ? this.finalLevelFrameBufferId : this.getRenderTarget().field_1476;
   }

   @Override
   public void clearTargetFrameBuffer() {
      this.finalLevelFrameBufferId = -1;
   }

   @Override
   public int getGlDepthTextureId() {
      return this.getRenderTarget().method_30278();
   }

   @Override
   public int getGlColorTextureId() {
      return this.getRenderTarget().method_30277();
   }

   @Override
   public int getTargetFramebufferViewportWidth() {
      return this.getRenderTarget().field_1480;
   }

   @Override
   public int getTargetFramebufferViewportHeight() {
      return this.getRenderTarget().field_1477;
   }

   @Override
   public boolean isFogStateSpecial() {
      boolean isBlind = this.playerHasBlindingEffect();
      return MC.field_1773.method_19418().method_19334() != class_5636.field_27888 || isBlind;
   }

   @Override
   public ILightMapWrapper getLightmapWrapper(@NotNull ILevelWrapper level) {
      return this.lightmapByDimensionType.get(level.getDimensionType());
   }

   public void updateLightmap(class_1011 lightPixels) {
      IClientLevelWrapper clientLevel = getLightmapClientLevelWrapper();
      if (clientLevel != null) {
         IDimensionTypeWrapper dimensionType = clientLevel.getDimensionType();
         LightMapWrapper_fabric wrapper = this.lightmapByDimensionType.computeIfAbsent(dimensionType, dimType -> new LightMapWrapper_fabric());
         wrapper.uploadLightmap(lightPixels);
      }
   }

   public void setLightmapId(int textureId) {
      IClientLevelWrapper clientLevel = getLightmapClientLevelWrapper();
      if (clientLevel != null) {
         IDimensionTypeWrapper dimensionType = clientLevel.getDimensionType();
         LightMapWrapper_fabric wrapper = this.lightmapByDimensionType.computeIfAbsent(dimensionType, dimType -> new LightMapWrapper_fabric());
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
