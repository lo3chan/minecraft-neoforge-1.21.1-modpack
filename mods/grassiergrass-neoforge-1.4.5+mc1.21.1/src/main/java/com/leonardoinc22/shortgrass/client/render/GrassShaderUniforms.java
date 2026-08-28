/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassGeometry;
import com.leonardoinc22.shortgrass.client.render.GrassTrailField;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class GrassShaderUniforms {
    private static final float NOISE_SCALE = 0.8f;
    private static final float WIND_BASE_ANGLE = 0.0f;
    private static final float DEFAULT_NOISE_SCROLL_SPEED = 0.005f;
    private static final float MAX_WIND_SCROLL_DELTA_TICKS = 5.0f;
    private static final float NANOSECONDS_TO_TICKS = 2.0E-8f;
    private static final float WIND_FLUTTER_IDLE_SPEED = 0.1f;
    private static final float WIND_FLUTTER_ACTIVE_SPEED = 0.24f;
    private static final float WIND_FLUTTER_WRAP = 402.12387f;
    private static final float PLANT_BOB_SPEED = 0.5f;
    private static boolean windScrollInitialized;
    private static long lastFrameNanos;
    private static float renderAnimationTicks;
    private static float lastWindScrollTime;
    private static float windNoiseScrollX;
    private static float windNoiseScrollZ;
    private static float windFlutterPhase;
    private static float plantWavePhase;
    private static float currentWindX;
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
        }
        long elapsedNanos = now - lastFrameNanos;
        lastFrameNanos = now;
        float deltaTicks = (float)elapsedNanos * 2.0E-8f;
        if (deltaTicks > 0.0f && Float.isFinite(deltaTicks)) {
            renderAnimationTicks += Math.min(deltaTicks, 5.0f);
        }
        return renderAnimationTicks;
    }

    static void updateAnimationUniforms(ShaderInstance shader, float windTime, Vec3 cameraPos) {
        shader.safeGetUniform("NoiseScale").set(0.8f);
        GrassShaderUniforms.setSharedWindUniforms(shader, windTime, cameraPos);
        boolean segmented = GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED;
        shader.safeGetUniform("StyleVanilla").set(segmented ? 1.0f : 0.0f);
        float bladeVisualHeight = GrassConfig.bladeHeight * (segmented ? 1.45f : 1.45f);
        shader.safeGetUniform("BladeVisualHeight").set(bladeVisualHeight);
        shader.safeGetUniform("BladeGradientBottom").set(GrassConfig.bladeGradientBottom);
        shader.safeGetUniform("BladeGradientTop").set(GrassConfig.bladeGradientTop);
        shader.safeGetUniform("BladeGradientCurve").set(GrassConfig.bladeGradientCurve);
        shader.safeGetUniform("BladeWidth").set(GrassConfig.bladeWidth);
        shader.safeGetUniform("HeightVariation").set(GrassConfig.heightVariation);
    }

    static void setSharedWindUniforms(ShaderInstance shader, float windTime, Vec3 cameraPos) {
        shader.safeGetUniform("NoiseScale").set(0.8f);
        GrassShaderUniforms.updateFrameState(windTime, cameraPos);
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
        float windSpeed = Mth.clamp((float)GrassConfig.effectiveWindSpeed(), (float)0.0f, (float)500.0f) / 100.0f;
        float windAngle = 0.0f + (float)Math.toRadians(GrassConfig.windDirectionDegrees);
        float windX = Mth.cos((float)windAngle);
        float windZ = Mth.sin((float)windAngle);
        GrassShaderUniforms.updateWindAnimation(windTime, windX, windZ, windSpeed);
        currentWindX = windX;
        currentWindZ = windZ;
        currentWindSpeed = windSpeed;
        currentTrailOriginX = GrassTrailField.originOffsetX(cameraPos);
        currentTrailOriginZ = GrassTrailField.originOffsetZ(cameraPos);
        currentTrailInverseWorldSize = GrassTrailField.inverseWorldSize();
        currentTrailStrength = GrassTrailField.shaderStrength();
        float noiseTile = 32.0f;
        currentTrailNoiseOriginX = GrassShaderUniforms.modTile(GrassTrailField.trailOriginWorldXExact(), noiseTile);
        currentTrailNoiseOriginZ = GrassShaderUniforms.modTile(GrassTrailField.trailOriginWorldZExact(), noiseTile);
    }

    private static float modTile(double value, float tile) {
        return (float)(value - Math.floor(value / (double)tile) * (double)tile);
    }

    private static void updateWindAnimation(float windTime, float windX, float windZ, float windSpeed) {
        if (!windScrollInitialized) {
            windScrollInitialized = true;
            lastWindScrollTime = windTime;
            return;
        }
        float deltaTicks = windTime - lastWindScrollTime;
        lastWindScrollTime = windTime;
        if (deltaTicks <= 0.0f) {
            return;
        }
        deltaTicks = Math.min(deltaTicks, 5.0f);
        float scrollAmount = 0.005f * windSpeed * deltaTicks;
        windNoiseScrollX = GrassShaderUniforms.wrapUnit(windNoiseScrollX - windX * scrollAmount);
        windNoiseScrollZ = GrassShaderUniforms.wrapUnit(windNoiseScrollZ - windZ * scrollAmount);
        float flutterSpeed = Mth.lerp((float)Mth.clamp((float)windSpeed, (float)0.0f, (float)1.0f), (float)0.1f, (float)0.24f);
        windFlutterPhase = GrassShaderUniforms.wrap(windFlutterPhase + flutterSpeed * deltaTicks, 402.12387f);
        plantWavePhase = GrassShaderUniforms.wrapRadians(plantWavePhase + Math.max(windSpeed, 0.001f) * 0.5f * deltaTicks);
    }

    private static float wrapUnit(float value) {
        return value - (float)Mth.floor((float)value);
    }

    private static float wrapRadians(float value) {
        return value - (float)Mth.floor((float)(value / ((float)Math.PI * 2))) * ((float)Math.PI * 2);
    }

    private static float wrap(float value, float period) {
        return value - (float)Mth.floor((float)(value / period)) * period;
    }

    static void resetWindScroll() {
        windScrollInitialized = false;
        lastFrameNanos = 0L;
        renderAnimationTicks = 0.0f;
        lastWindScrollTime = 0.0f;
        windNoiseScrollX = 0.0f;
        windNoiseScrollZ = 0.0f;
        windFlutterPhase = 0.0f;
        plantWavePhase = 0.0f;
        currentWindX = 1.0f;
        currentWindZ = 0.0f;
        currentWindSpeed = 0.0f;
        currentTrailOriginX = 0.0f;
        currentTrailOriginZ = 0.0f;
        currentTrailInverseWorldSize = 0.0f;
        currentTrailStrength = 0.0f;
        currentTrailNoiseOriginX = 0.0f;
        currentTrailNoiseOriginZ = 0.0f;
    }

    static void close() {
        GrassShaderUniforms.resetWindScroll();
    }

    static float noiseScale() {
        return 0.8f;
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
        return GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED ? 1.0f : 0.0f;
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

    static {
        currentWindX = 1.0f;
    }
}

