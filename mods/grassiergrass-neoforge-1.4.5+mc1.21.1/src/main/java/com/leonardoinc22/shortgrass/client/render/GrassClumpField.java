/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.logging.LogUtils
 *  net.minecraft.util.Mth
 *  org.slf4j.Logger
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassGeometry;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.InputStream;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

final class GrassClumpField {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PATH = "/assets/grassiergrass/textures/effect/noise.png";
    private static final float RANDOM_PRIORITY_SHARE = 0.15f;
    private static final float PER_BLADE_HEIGHT_MIN = 0.88f;
    private static final float PER_BLADE_HEIGHT_MAX = 1.14f;
    private static final float HEIGHT_NOISE_SCALE = 1.0f;
    private static final float HEIGHT_NOISE_CONTRAST = 1.35f;
    private static final float HEIGHT_NOISE_BIAS = 0.5f;
    private static final float HEIGHT_NOISE_SHORT_MULTIPLIER = 0.1f;
    private static final float HEIGHT_NOISE_TALL_MULTIPLIER = 1.28f;
    private static final float REGIONAL_LENGTH_FRACTION = 0.59f;
    private static final float MIN_BLADE_LENGTH = 0.02f;
    private static byte[] snowCoverage;
    private static int width;
    private static int height;
    private static boolean loadFailed;

    private GrassClumpField() {
    }

    static boolean keepBlade(float noiseX, float noiseZ, float sparsity, float bladeRandom) {
        if (sparsity <= 0.0f) {
            return true;
        }
        if (sparsity >= 1.0f) {
            return false;
        }
        if (snowCoverage == null) {
            GrassClumpField.load();
        }
        if (snowCoverage == null) {
            return Mth.clamp((float)bladeRandom, (float)0.0f, (float)1.0f) < 1.0f - sparsity;
        }
        float shortPriority = 1.0f - GrassClumpField.stationaryHeightNoise(noiseX, noiseZ);
        float removalPriority = shortPriority * 0.85f + Mth.clamp((float)bladeRandom, (float)0.0f, (float)1.0f) * 0.15f;
        return removalPriority < 1.0f - sparsity;
    }

    static float bladeLengthMultiplier(float noiseX, float noiseZ, float angle, int heightClass) {
        if (snowCoverage == null) {
            GrassClumpField.load();
        }
        if (snowCoverage == null) {
            return 1.0f;
        }
        float heightMultiplier = Mth.lerp((float)GrassClumpField.stationaryHeightNoise(noiseX, noiseZ), (float)0.1f, (float)1.28f);
        int angleBucket = Mth.clamp((int)((int)(GrassClumpField.positiveAngle(angle) / ((float)Math.PI * 2) * 31.0f)), (int)0, (int)31);
        float bladeRandom = GrassClumpField.hash2D(noiseX + (float)angleBucket * 0.071f, noiseZ + (float)heightClass * 13.37f);
        float shapedRandom = GrassClumpField.smoothstep(0.0f, 1.0f, bladeRandom);
        float perBladeHeight = Mth.lerp((float)shapedRandom, (float)0.88f, (float)1.14f);
        float variation = ((heightMultiplier - 1.0f) * 0.59f + (perBladeHeight - 1.0f)) * GrassConfig.heightVariation;
        return Math.max(1.0f + variation, 0.02f / GrassGeometry.visualBladeHeight());
    }

    private static float stationaryHeightNoise(float noiseX, float noiseZ) {
        float noise = GrassClumpField.sampleSmoothNoise(noiseX * 1.0f, noiseZ * 1.0f);
        noise = Mth.clamp((float)((noise - 0.5f) * 1.35f + 0.5f), (float)0.0f, (float)1.0f);
        return GrassClumpField.smoothstep(0.0f, 1.0f, noise);
    }

    static float snowCoverage(float noiseX, float noiseZ) {
        if (snowCoverage == null) {
            GrassClumpField.load();
        }
        if (snowCoverage == null) {
            return 0.5f;
        }
        return GrassClumpField.sampleSmoothNoise(noiseX, noiseZ);
    }

    private static float sampleSmoothNoise(float noiseX, float noiseZ) {
        float x = GrassClumpField.fract(noiseX) * (float)width - 0.5f;
        float y = GrassClumpField.fract(noiseZ) * (float)height - 0.5f;
        int x0 = Mth.floor((float)x);
        int y0 = Mth.floor((float)y);
        float tx = GrassClumpField.smoothstep(0.0f, 1.0f, x - (float)x0);
        float ty = GrassClumpField.smoothstep(0.0f, 1.0f, y - (float)y0);
        float bottom = Mth.lerp((float)tx, (float)GrassClumpField.coverageAt(x0, y0), (float)GrassClumpField.coverageAt(x0 + 1, y0));
        float top = Mth.lerp((float)tx, (float)GrassClumpField.coverageAt(x0, y0 + 1), (float)GrassClumpField.coverageAt(x0 + 1, y0 + 1));
        return Mth.lerp((float)ty, (float)bottom, (float)top);
    }

    private static float coverageAt(int x, int y) {
        int index = GrassClumpField.wrap(y, height) * width + GrassClumpField.wrap(x, width);
        return (float)(snowCoverage[index] & 0xFF) / 255.0f;
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = Mth.clamp((float)((value - edge0) / (edge1 - edge0)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static float hash2D(float x, float y) {
        float px = GrassClumpField.fract(x * 0.1031f);
        float py = GrassClumpField.fract(y * 0.1031f);
        float pz = GrassClumpField.fract(x * 0.1031f);
        float dot = px * (py + 33.33f) + py * (pz + 33.33f) + pz * (px + 33.33f);
        return GrassClumpField.fract(((px += dot) + (py += dot)) * (pz += dot));
    }

    private static float fract(float value) {
        return value - (float)Mth.floor((float)value);
    }

    private static float positiveAngle(float angle) {
        return angle - (float)Mth.floor((float)(angle / ((float)Math.PI * 2))) * ((float)Math.PI * 2);
    }

    private static int wrap(int value, int size) {
        int wrapped = value % size;
        return wrapped < 0 ? wrapped + size : wrapped;
    }

    private static synchronized void load() {
        if (snowCoverage != null || loadFailed) {
            return;
        }
        try (InputStream in = GrassClumpField.class.getResourceAsStream(PATH);
             NativeImage image = NativeImage.read((InputStream)in);){
            width = image.getWidth();
            height = image.getHeight();
            byte[] loadedCoverage = new byte[width * height];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int rgba = image.getPixelRGBA(x, y);
                    loadedCoverage[y * GrassClumpField.width + x] = (byte)(rgba & 0xFF);
                }
            }
            snowCoverage = loadedCoverage;
        }
        catch (Exception e) {
            loadFailed = true;
            LOGGER.error("[grassiergrass] failed to load noise.png height field", (Throwable)e);
        }
    }
}

