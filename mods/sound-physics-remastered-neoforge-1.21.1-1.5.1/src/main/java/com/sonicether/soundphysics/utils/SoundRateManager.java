/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.resources.ResourceLocation
 */
package com.sonicether.soundphysics.utils;

import com.sonicether.soundphysics.SoundPhysicsMod;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

public class SoundRateManager {
    private static final Map<ResourceLocation, Integer> soundCounts = new ConcurrentHashMap<ResourceLocation, Integer>();
    private static boolean worldInitialized;

    private SoundRateManager() {
    }

    public static boolean incrementAndCheckLimit(ResourceLocation sound) {
        int max;
        int count = soundCounts.getOrDefault(sound, 0);
        if (count >= (max = SoundPhysicsMod.SOUND_RATE_CONFIG.getMaxCount(sound).intValue())) {
            return true;
        }
        soundCounts.put(sound, count + 1);
        return false;
    }

    public static boolean isWorldInitialized() {
        return worldInitialized;
    }

    public static void onClientTick(ClientLevel level) {
        SoundRateManager.clear();
        worldInitialized = true;
    }

    public static void clear() {
        soundCounts.clear();
    }

    public static void onLoadLevel(ClientLevel clientLevel) {
        SoundRateManager.clear();
        worldInitialized = false;
    }

    public static void onUnloadLevel(ClientLevel clientLevel) {
        SoundRateManager.clear();
        worldInitialized = false;
    }
}

