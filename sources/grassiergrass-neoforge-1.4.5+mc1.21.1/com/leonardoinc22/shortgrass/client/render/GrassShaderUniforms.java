package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class GrassShaderUniforms {
   private static final float NOISE_SCALE = 0.8F;
   private static final float WIND_BASE_ANGLE = 0.0F;
   private static final float DEFAULT_NOISE_SCROLL_SPEED = 0.005F;
   private static final float MAX_WIND_SCROLL_DELTA_TICKS = 5.0F;
   private static final float NANOSECONDS_TO_TICKS = 2.0E-8F;
   private static final float WIND_FLUTTER_IDLE_SPEED = 0.1F;
   private static final float WIND_FLUTTER_ACTIVE_SPEED = 0.24F;
   private static final float WIND_FLUTTER_WRAP = 402.12387F;
   private static final float PLANT_BOB_SPEED = 0.5F;
   private static boolean windScrollInitialized;
   private static long lastFrameNanos;
   private static float renderAnimationTicks;
   private static float lastWindScrollTime;
   private static float windNoiseScrollX;
   private static float windNoiseScrollZ;
   private static float windFlutterPhase;
   private static float plantWavePhase;
   private static float currentWindX = 1.0F;
   private static float currentWindZ;
   private static float currentWindSpeed;
   private static float currentTrailOriginX;
   private static float currentTrailOriginZ;
   private static float currentTrailInverseWorldSize;
   private static float currentTrailStrength;
   private static float currentTrailNoiseOriginX;
   private static float currentTrailNoiseOriginZ;

   private GrassShaderUniforms() {
   }

   static float windDirX() {
      return currentWindX;
   }

   static float windDirZ() {
      return currentWindZ;
   }

   static float advanceRenderAnimationTicks() {
      long now = System.nanoTime();
      if (lastFrameNanos == 0L) {
         lastFrameNanos = now;
         return renderAnimationTicks;
      } else {
         long elapsedNanos = now - lastFrameNanos;
         lastFrameNanos = now;
         float deltaTicks = (float)elapsedNanos * 2.0E-8F;
         if (deltaTicks > 0.0F && Float.isFinite(deltaTicks)) {
            renderAnimationTicks = renderAnimationTicks + Math.min(deltaTicks, 5.0F);
         }

         return renderAnimationTicks;
      }
   }

   static void updateAnimationUniforms(ShaderInstance shader, float windTime, Vec3 cameraPos) {
      shader.safeGetUniform("NoiseScale").set(0.8F);
      setSharedWindUniforms(shader, windTime, cameraPos);
      boolean segmented = GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED;
      shader.safeGetUniform("StyleVanilla").set(segmented ? 1.0F : 0.0F);
      float bladeVisualHeight = GrassConfig.bladeHeight * (segmented ? 1.45F : 1.45F);
      shader.safeGetUniform("BladeVisualHeight").set(bladeVisualHeight);
      shader.safeGetUniform("BladeGradientBottom").set(GrassConfig.bladeGradientBottom);
      shader.safeGetUniform("BladeGradientTop").set(GrassConfig.bladeGradientTop);
      shader.safeGetUniform("BladeGradientCurve").set(GrassConfig.bladeGradientCurve);
      shader.safeGetUniform("BladeWidth").set(GrassConfig.bladeWidth);
      shader.safeGetUniform("HeightVariation").set(GrassConfig.heightVariation);
   }

   static void setSharedWindUniforms(ShaderInstance shader, float windTime, Vec3 cameraPos) {
      shader.safeGetUniform("NoiseScale").set(0.8F);
      updateFrameState(windTime, cameraPos);
      shader.safeGetUniform("NoiseScrollOffset").set(windNoiseScrollX, windNoiseScrollZ);
      shader.safeGetUniform("WindDirection").set(currentWindX, currentWindZ);
      shader.safeGetUniform("WindFlutterPhase").set(windFlutterPhase);
      shader.safeGetUniform("PlantBobPhase").set(plantWavePhase);
      shader.safeGetUniform("WindStrength").set(currentWindSpeed);
      shader.safeGetUniform("GrassBrightness").set(GrassConfig.grassBrightness);
      shader.safeGetUniform("TrailParams").set(currentTrailOriginX, currentTrailOriginZ, currentTrailInverseWorldSize, currentTrailStrength);
      shader.safeGetUniform("TrailNoiseOrigin").set(currentTrailNoiseOriginX, currentTrailNoiseOriginZ);
   }

   static void updateFrameState(float windTime, Vec3 cameraPos) {
      GrassConfig.updateDynamicWind(windTime);
      float windSpeed = Mth.clamp(GrassConfig.effectiveWindSpeed(), 0.0F, 500.0F) / 100.0F;
      float windAngle = 0.0F + (float)Math.toRadians(GrassConfig.windDirectionDegrees);
      float windX = Mth.cos(windAngle);
      float windZ = Mth.sin(windAngle);
      updateWindAnimation(windTime, windX, windZ, windSpeed);
      currentWindX = windX;
      currentWindZ = windZ;
      currentWindSpeed = windSpeed;
      currentTrailOriginX = GrassTrailField.originOffsetX(cameraPos);
      currentTrailOriginZ = GrassTrailField.originOffsetZ(cameraPos);
      currentTrailInverseWorldSize = GrassTrailField.inverseWorldSize();
      currentTrailStrength = GrassTrailField.shaderStrength();
      float noiseTile = 32.0F;
      currentTrailNoiseOriginX = modTile(GrassTrailField.trailOriginWorldXExact(), noiseTile);
      currentTrailNoiseOriginZ = modTile(GrassTrailField.trailOriginWorldZExact(), noiseTile);
   }

   private static float modTile(double value, float tile) {
      return (float)(value - Math.floor(value / tile) * tile);
   }

   private static void updateWindAnimation(float windTime, float windX, float windZ, float windSpeed) {
      if (!windScrollInitialized) {
         windScrollInitialized = true;
         lastWindScrollTime = windTime;
      } else {
         float deltaTicks = windTime - lastWindScrollTime;
         lastWindScrollTime = windTime;
         if (!(deltaTicks <= 0.0F)) {
            deltaTicks = Math.min(deltaTicks, 5.0F);
            float scrollAmount = 0.005F * windSpeed * deltaTicks;
            windNoiseScrollX = wrapUnit(windNoiseScrollX - windX * scrollAmount);
            windNoiseScrollZ = wrapUnit(windNoiseScrollZ - windZ * scrollAmount);
            float flutterSpeed = Mth.lerp(Mth.clamp(windSpeed, 0.0F, 1.0F), 0.1F, 0.24F);
            windFlutterPhase = wrap(windFlutterPhase + flutterSpeed * deltaTicks, 402.12387F);
            plantWavePhase = wrapRadians(plantWavePhase + Math.max(windSpeed, 0.001F) * 0.5F * deltaTicks);
         }
      }
   }

   private static float wrapUnit(float value) {
      return value - Mth.floor(value);
   }

   private static float wrapRadians(float value) {
      return value - Mth.floor(value / 6.2831855F) * 6.2831855F;
   }

   private static float wrap(float value, float period) {
      return value - Mth.floor(value / period) * period;
   }

   static void resetWindScroll() {
      windScrollInitialized = false;
      lastFrameNanos = 0L;
      renderAnimationTicks = 0.0F;
      lastWindScrollTime = 0.0F;
      windNoiseScrollX = 0.0F;
      windNoiseScrollZ = 0.0F;
      windFlutterPhase = 0.0F;
      plantWavePhase = 0.0F;
      currentWindX = 1.0F;
      currentWindZ = 0.0F;
      currentWindSpeed = 0.0F;
      currentTrailOriginX = 0.0F;
      currentTrailOriginZ = 0.0F;
      currentTrailInverseWorldSize = 0.0F;
      currentTrailStrength = 0.0F;
      currentTrailNoiseOriginX = 0.0F;
      currentTrailNoiseOriginZ = 0.0F;
   }

   static void close() {
      resetWindScroll();
   }

   static float noiseScale() {
      return 0.8F;
   }

   static float windX() {
      return currentWindX;
   }

   static float windZ() {
      return currentWindZ;
   }

   static float windStrength() {
      return currentWindSpeed;
   }

   static float noiseScrollX() {
      return windNoiseScrollX;
   }

   static float noiseScrollZ() {
      return windNoiseScrollZ;
   }

   static float windFlutterPhase() {
      return windFlutterPhase;
   }

   static float plantBobPhase() {
      return plantWavePhase;
   }

   static float styleVanilla() {
      return GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED ? 1.0F : 0.0F;
   }

   static float bladeVisualHeight() {
      return GrassGeometry.visualBladeHeight();
   }

   static float bladeWidth() {
      return GrassConfig.bladeWidth;
   }

   static float heightVariation() {
      return GrassConfig.heightVariation;
   }

   static float trailOriginX() {
      return currentTrailOriginX;
   }

   static float trailOriginZ() {
      return currentTrailOriginZ;
   }

   static float trailInverseWorldSize() {
      return currentTrailInverseWorldSize;
   }

   static float trailStrength() {
      return currentTrailStrength;
   }

   static float trailNoiseOriginX() {
      return currentTrailNoiseOriginX;
   }

   static float trailNoiseOriginZ() {
      return currentTrailNoiseOriginZ;
   }
}
