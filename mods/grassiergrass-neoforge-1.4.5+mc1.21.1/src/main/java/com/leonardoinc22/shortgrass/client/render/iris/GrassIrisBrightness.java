/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.util.Mth
 */
package com.leonardoinc22.shortgrass.client.render.iris;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public final class GrassIrisBrightness {
    private static final float BLADE_MULTIPLIER = 0.7f;
    private static final float PLANT_MULTIPLIER = 1.0f;

    private GrassIrisBrightness() {
    }

    private static float brightness(boolean plants) {
        return GrassConfig.grassBrightness * (plants ? 1.0f : 0.7f);
    }

    public static float colorMultiplier(boolean plants) {
        return Math.min(GrassIrisBrightness.brightness(plants), 1.0f);
    }

    public static float lightBrightness(boolean plants) {
        return Math.max(GrassIrisBrightness.brightness(plants), 1.0f);
    }

    public static int adjustLight(int light, boolean plants) {
        float blend = Mth.clamp((float)(GrassIrisBrightness.brightness(plants) - 1.0f), (float)0.0f, (float)1.0f);
        if (blend <= 0.0f) {
            return light;
        }
        int block = Mth.clamp((int)Math.round(Mth.lerp((float)blend, (float)LightTexture.block((int)light), (float)15.0f)), (int)0, (int)15);
        int sky = Mth.clamp((int)Math.round(Mth.lerp((float)blend, (float)LightTexture.sky((int)light), (float)15.0f)), (int)0, (int)15);
        return LightTexture.pack((int)block, (int)sky);
    }
}

