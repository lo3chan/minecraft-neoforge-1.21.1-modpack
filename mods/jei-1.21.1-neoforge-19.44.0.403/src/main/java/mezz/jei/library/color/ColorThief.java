/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.NativeImage$Format
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.color;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Arrays;
import mezz.jei.library.color.MMCQ;
import org.jetbrains.annotations.Nullable;

public class ColorThief {
    private static final int MIN_QUALITY = 1;

    public static int[][] getPalette(NativeImage sourceImage, int colorCount, int quality, boolean ignoreWhite) {
        MMCQ.CMap cmap = ColorThief.getColorMap(sourceImage, colorCount, quality, ignoreWhite);
        if (cmap == null) {
            return new int[0][0];
        }
        return cmap.palette();
    }

    @Nullable
    public static MMCQ.CMap getColorMap(NativeImage sourceImage, int colorCount, int quality, boolean ignoreWhite) {
        ColorThief.validateQuality(quality);
        if (sourceImage.format() == NativeImage.Format.RGBA) {
            int[][] pixelArray = ColorThief.getPixels(sourceImage, quality, ignoreWhite);
            return MMCQ.quantize(pixelArray, colorCount);
        }
        return null;
    }

    private static int[][] getPixels(NativeImage sourceImage, int quality, boolean ignoreWhite) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int pixelCount = width * height;
        int numRegardedPixels = (pixelCount + quality - 1) / quality;
        int numUsedPixels = 0;
        int[][] pixelArray = new int[numRegardedPixels][];
        int i = 0;
        while (i < pixelCount) {
            int x = i % width;
            int y = i / width;
            int rgba = sourceImage.getPixelRGBA(x, y);
            int a = rgba >> 24 & 0xFF;
            int b = rgba >> 16 & 0xFF;
            int g = rgba >> 8 & 0xFF;
            int r = rgba & 0xFF;
            if (!(a < 125 || ignoreWhite && r > 250 && g > 250 && b > 250)) {
                pixelArray[numUsedPixels] = new int[]{r, g, b};
                ++numUsedPixels;
                i += quality;
                continue;
            }
            ++i;
        }
        return (int[][])Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
    }

    private static void validateQuality(int quality) {
        if (quality < 1) {
            throw new IllegalArgumentException("quality must be at least 1");
        }
    }
}

