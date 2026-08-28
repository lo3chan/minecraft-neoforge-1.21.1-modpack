/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.Util$OS
 */
package me.flashyreese.mods.sodiumextra.client.util;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.Util;

public final class MacReducedResolution {
    private static boolean openGlBackend;

    public static boolean isEnabled() {
        return Util.getPlatform() == Util.OS.OSX && SodiumExtraClientMod.options().extraSettings.reduceResolutionOnMac;
    }

    public static int reduce(int value) {
        return Math.max(1, value / 2);
    }

    public static void useOpenGlBackend() {
        openGlBackend = true;
    }

    public static boolean shouldReduceFramebuffer() {
        return MacReducedResolution.isEnabled() && !openGlBackend;
    }

    public static boolean shouldUseWindowSizeForInitialFramebuffer() {
        return MacReducedResolution.isEnabled() && openGlBackend;
    }

    public static boolean shouldScalePresentation(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return MacReducedResolution.isEnabled() && (sourceWidth < targetWidth || sourceHeight < targetHeight);
    }
}

