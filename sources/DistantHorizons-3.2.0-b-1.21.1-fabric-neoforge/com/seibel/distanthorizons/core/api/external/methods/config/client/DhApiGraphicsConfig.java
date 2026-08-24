package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.enums.config.EDhApiBlocksToAvoid;
import com.seibel.distanthorizons.api.enums.config.EDhApiHorizontalQuality;
import com.seibel.distanthorizons.api.enums.config.EDhApiLodShading;
import com.seibel.distanthorizons.api.enums.config.EDhApiMaxHorizontalResolution;
import com.seibel.distanthorizons.api.enums.config.EDhApiVerticalQuality;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiRendererMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiAmbientOcclusionConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiFogConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiGenericRenderingConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiGraphicsConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiNoiseTextureConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;
import com.seibel.distanthorizons.core.config.api.converters.RenderModeEnabledConverter;

public class DhApiGraphicsConfig implements IDhApiGraphicsConfig {
   public static DhApiGraphicsConfig INSTANCE = new DhApiGraphicsConfig();

   private DhApiGraphicsConfig() {
   }

   @Override
   public IDhApiFogConfig fog() {
      return DhApiFogConfig.INSTANCE;
   }

   @Override
   public IDhApiAmbientOcclusionConfig ambientOcclusion() {
      return DhApiAmbientOcclusionConfig.INSTANCE;
   }

   @Override
   public IDhApiNoiseTextureConfig noiseTexture() {
      return DhApiNoiseTextureConfig.INSTANCE;
   }

   @Override
   public IDhApiGenericRenderingConfig genericRendering() {
      return DhApiGenericRenderingConfig.INSTANCE;
   }

   @Override
   public IDhApiConfigValue<Integer> chunkRenderDistance() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.lodChunkRenderDistanceRadius);
   }

   @Override
   public IDhApiConfigValue<Boolean> renderingEnabled() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.rendererMode, new RenderModeEnabledConverter());
   }

   @Override
   public IDhApiConfigValue<EDhApiRendererMode> renderingMode() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.rendererMode);
   }

   @Override
   public IDhApiConfigValue<EDhApiMaxHorizontalResolution> maxHorizontalResolution() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.maxHorizontalResolution);
   }

   @Override
   public IDhApiConfigValue<EDhApiVerticalQuality> verticalQuality() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.verticalQuality);
   }

   @Override
   public IDhApiConfigValue<EDhApiHorizontalQuality> horizontalQuality() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.horizontalQuality);
   }

   @Override
   public IDhApiConfigValue<Boolean> useCameraPositionForQualityDropOff() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.useCameraPositionForQualityDropOff);
   }

   @Override
   public IDhApiConfigValue<EDhApiTransparency> transparency() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.transparency);
   }

   @Override
   public IDhApiConfigValue<EDhApiBlocksToAvoid> blocksToAvoid() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.blocksToIgnore);
   }

   @Override
   public IDhApiConfigValue<Boolean> tintWithAvoidedBlocks() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.tintWithAvoidedBlocks);
   }

   @Override
   public IDhApiConfigValue<Integer> getBiomeBlending() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.lodBiomeBlending);
   }

   @Override
   public IDhApiConfigValue<Float> overdrawPreventionRadius() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.overdrawPrevention);
   }

   @Override
   public IDhApiConfigValue<Float> brightnessMultiplier() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.brightnessMultiplier);
   }

   @Override
   public IDhApiConfigValue<Float> saturationMultiplier() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.saturationMultiplier);
   }

   @Override
   public IDhApiConfigValue<Boolean> caveCullingEnabled() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.enableCaveCulling);
   }

   @Override
   public IDhApiConfigValue<Integer> caveCullingHeight() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.caveCullingHeight);
   }

   @Override
   public IDhApiConfigValue<Integer> earthCurvatureRatio() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Experimental.earthCurveRatio);
   }

   @Override
   public IDhApiConfigValue<Boolean> lodOnlyMode() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.lodOnlyMode);
   }

   @Override
   public IDhApiConfigValue<EDhApiLodShading> lodShading() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Quality.lodShading);
   }

   @Override
   public IDhApiConfigValue<Boolean> disableFrustumCulling() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.disableFrustumCulling);
   }

   @Override
   public IDhApiConfigValue<Boolean> disableShadowFrustumCulling() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Culling.disableShadowPassFrustumCulling);
   }
}
