/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.MipmapGenerator
 *  net.minecraft.util.FastColor$ABGR32
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Unique
 */
package net.irisshaders.iris.mixin.bettermipmaps;

import net.irisshaders.iris.helpers.ColorSRGB;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={MipmapGenerator.class})
public class MixinMipmapGenerator {
    @Overwrite
    private static int alphaBlend(int one, int two, int three, int four, boolean checkAlpha) {
        return MixinMipmapGenerator.weightedAverageColor(MixinMipmapGenerator.weightedAverageColor(one, two), MixinMipmapGenerator.weightedAverageColor(three, four));
    }

    @Unique
    private static int weightedAverageColor(int one, int two) {
        int alphaTwo;
        int alphaOne = FastColor.ABGR32.alpha((int)one);
        if (alphaOne == (alphaTwo = FastColor.ABGR32.alpha((int)two))) {
            return MixinMipmapGenerator.averageRgb(one, two, alphaOne);
        }
        if (alphaOne == 0) {
            return two & 0xFFFFFF | alphaTwo >> 2 << 24;
        }
        if (alphaTwo == 0) {
            return one & 0xFFFFFF | alphaOne >> 2 << 24;
        }
        float scale = 1.0f / (float)(alphaOne + alphaTwo);
        float relativeWeightOne = (float)alphaOne * scale;
        float relativeWeightTwo = (float)alphaTwo * scale;
        float oneR = ColorSRGB.srgbToLinear(FastColor.ABGR32.red((int)one)) * relativeWeightOne;
        float oneG = ColorSRGB.srgbToLinear(FastColor.ABGR32.green((int)one)) * relativeWeightOne;
        float oneB = ColorSRGB.srgbToLinear(FastColor.ABGR32.blue((int)one)) * relativeWeightOne;
        float twoR = ColorSRGB.srgbToLinear(FastColor.ABGR32.red((int)two)) * relativeWeightTwo;
        float twoG = ColorSRGB.srgbToLinear(FastColor.ABGR32.green((int)two)) * relativeWeightTwo;
        float twoB = ColorSRGB.srgbToLinear(FastColor.ABGR32.blue((int)two)) * relativeWeightTwo;
        float linearR = oneR + twoR;
        float linearG = oneG + twoG;
        float linearB = oneB + twoB;
        int averageAlpha = alphaOne + alphaTwo >> 1;
        return ColorSRGB.linearToSrgb(linearR, linearG, linearB, averageAlpha);
    }

    @Unique
    private static int averageRgb(int a, int b, int alpha) {
        float ar = ColorSRGB.srgbToLinear(FastColor.ABGR32.red((int)a));
        float ag = ColorSRGB.srgbToLinear(FastColor.ABGR32.green((int)a));
        float ab = ColorSRGB.srgbToLinear(FastColor.ABGR32.blue((int)a));
        float br = ColorSRGB.srgbToLinear(FastColor.ABGR32.red((int)b));
        float bg = ColorSRGB.srgbToLinear(FastColor.ABGR32.green((int)b));
        float bb = ColorSRGB.srgbToLinear(FastColor.ABGR32.blue((int)b));
        return ColorSRGB.linearToSrgb((ar + br) * 0.5f, (ag + bg) * 0.5f, (ab + bb) * 0.5f, alpha);
    }
}

