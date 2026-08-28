/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 */
package com.sonicether.soundphysics.utils;

import java.util.regex.Pattern;
import net.minecraft.sounds.SoundEvent;

public class SoundUtils {
    private static final Pattern STEP_PATTERN = Pattern.compile(".*step.*");

    public static double calculateEntitySoundYOffset(float standingEyeHeight, SoundEvent sound) {
        if (STEP_PATTERN.matcher(sound.getLocation().getPath()).matches()) {
            return 0.0;
        }
        return standingEyeHeight;
    }
}

