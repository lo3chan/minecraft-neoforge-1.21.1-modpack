/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package com.leonardoinc22.shortgrass.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.io.InputStream;
import org.slf4j.Logger;

final class GrassHueNoise {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PATH = "/assets/grassiergrass/textures/effect/grainy.png";
    static final float HUE_VARIATION_RANGE = 0.04f;
    static final float HUE_TILE_BLOCKS = 96.0f;
    private static byte[] luminance;
    private static int size;
    private static boolean loadFailed;

    private GrassHueNoise() {
    }

    static int shiftHue(int tint, float worldX, float worldZ, float bladeRandom, float bladeHueJitterDegrees) {
        float grainy = GrassHueNoise.sample(worldX / 96.0f, worldZ / 96.0f);
        float delta = (grainy - 0.5f) * 0.04f + (bladeRandom - 0.5f) * (bladeHueJitterDegrees / 180.0f);
        if (delta == 0.0f) {
            return tint;
        }
        float[] hsb = Color.RGBtoHSB(tint >> 16 & 0xFF, tint >> 8 & 0xFF, tint & 0xFF, null);
        hsb[0] = (hsb[0] + delta) % 1.0f;
        if (hsb[0] < 0.0f) {
            hsb[0] = hsb[0] + 1.0f;
        }
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0xFFFFFF;
    }

    private static float sample(float u, float v) {
        if (luminance == null) {
            GrassHueNoise.load();
        }
        if (luminance == null) {
            return 0.5f;
        }
        int x = GrassHueNoise.wrap((int)Math.floor(u * (float)size));
        int y = GrassHueNoise.wrap((int)Math.floor(v * (float)size));
        return (float)(luminance[y * size + x] & 0xFF) / 255.0f;
    }

    private static int wrap(int i) {
        return (i %= size) < 0 ? i + size : i;
    }

    private static synchronized void load() {
        if (luminance != null || loadFailed) {
            return;
        }
        try (InputStream in = GrassHueNoise.class.getResourceAsStream(PATH);
             NativeImage image = NativeImage.read((InputStream)in);){
            size = image.getWidth();
            byte[] lum = new byte[size * image.getHeight()];
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < size; ++x) {
                    lum[y * GrassHueNoise.size + x] = (byte)(image.getPixelRGBA(x, y) & 0xFF);
                }
            }
            luminance = lum;
        }
        catch (Exception e) {
            loadFailed = true;
            LOGGER.error("[grassiergrass] failed to load grainy.png hue noise", (Throwable)e);
        }
    }
}

