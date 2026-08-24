package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.enums.config.EDhApiBlocksToAvoid;
import com.seibel.distanthorizons.api.enums.config.EDhApiHorizontalQuality;
import com.seibel.distanthorizons.api.enums.config.EDhApiLodShading;
import com.seibel.distanthorizons.api.enums.config.EDhApiMaxHorizontalResolution;
import com.seibel.distanthorizons.api.enums.config.EDhApiVerticalQuality;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiRendererMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiGraphicsConfig extends IDhApiConfigGroup {
   IDhApiFogConfig fog();

   IDhApiAmbientOcclusionConfig ambientOcclusion();

   IDhApiNoiseTextureConfig noiseTexture();

   IDhApiGenericRenderingConfig genericRendering();

   IDhApiConfigValue<Integer> chunkRenderDistance();

   IDhApiConfigValue<Boolean> renderingEnabled();

   IDhApiConfigValue<EDhApiRendererMode> renderingMode();

   IDhApiConfigValue<EDhApiMaxHorizontalResolution> maxHorizontalResolution();

   IDhApiConfigValue<EDhApiVerticalQuality> verticalQuality();

   IDhApiConfigValue<EDhApiHorizontalQuality> horizontalQuality();

   IDhApiConfigValue<Boolean> useCameraPositionForQualityDropOff();

   IDhApiConfigValue<EDhApiTransparency> transparency();

   IDhApiConfigValue<EDhApiBlocksToAvoid> blocksToAvoid();

   IDhApiConfigValue<Boolean> tintWithAvoidedBlocks();

   IDhApiConfigValue<Integer> getBiomeBlending();

   IDhApiConfigValue<Float> overdrawPreventionRadius();

   IDhApiConfigValue<Float> brightnessMultiplier();

   IDhApiConfigValue<Float> saturationMultiplier();

   IDhApiConfigValue<Boolean> caveCullingEnabled();

   IDhApiConfigValue<Integer> caveCullingHeight();

   IDhApiConfigValue<Integer> earthCurvatureRatio();

   IDhApiConfigValue<Boolean> lodOnlyMode();

   IDhApiConfigValue<EDhApiLodShading> lodShading();

   IDhApiConfigValue<Boolean> disableFrustumCulling();

   IDhApiConfigValue<Boolean> disableShadowFrustumCulling();
}
