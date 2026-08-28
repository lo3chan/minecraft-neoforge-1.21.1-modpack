/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package dev.tr7zw.waveycapes.support;

import dev.tr7zw.waveycapes.support.AnimationSupport;
import dev.tr7zw.waveycapes.support.ModSupport;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import lombok.Generated;

public class SupportManager {
    public static Set<ModSupport> mods = new HashSet<ModSupport>();
    public static Set<AnimationSupport> animationSupport = new HashSet<AnimationSupport>();
    public static Supplier<Float> alphaSupplier = () -> Float.valueOf(1.0f);

    public static Set<ModSupport> getSupportedMods() {
        return mods;
    }

    @Generated
    public static Supplier<Float> getAlphaSupplier() {
        return alphaSupplier;
    }

    @Generated
    public static void setAlphaSupplier(Supplier<Float> alphaSupplier) {
        SupportManager.alphaSupplier = alphaSupplier;
    }
}

