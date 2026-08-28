/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.Vec3
 */
package com.sonicether.soundphysics.utils;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.profiling.TaskProfiler;
import com.sonicether.soundphysics.utils.SoundRateManager;
import com.sonicether.soundphysics.world.CachingClientLevel;
import com.sonicether.soundphysics.world.ClientLevelProxy;
import com.sonicether.soundphysics.world.ClonedClientLevel;
import com.sonicether.soundphysics.world.UnsafeClientLevel;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class LevelAccessUtils {
    private static final TaskProfiler PROFILER = new TaskProfiler("Level Caching");
    private static final Minecraft MC = Minecraft.getInstance();

    public static void onLoadLevel(ClientLevel clientLevel) {
        Loggers.logDebug("Creating initial level cache", new Object[0]);
        LevelAccessUtils.updateLevelCache(clientLevel, LevelAccessUtils.levelOriginFromPlayer(), clientLevel.getGameTime());
        SoundRateManager.onLoadLevel(clientLevel);
    }

    public static void onUnloadLevel(ClientLevel clientLevel) {
        Loggers.logDebug("Removing level cache due to level unload", new Object[0]);
        ((CachingClientLevel)clientLevel).sound_physics_remastered$setCachedClone(null);
        SoundRateManager.onUnloadLevel(clientLevel);
    }

    public static void tickLevelCache(ClientLevel clientLevel) {
        if (!SoundPhysicsMod.CONFIG.enabled.get().booleanValue()) {
            return;
        }
        if (SoundPhysicsMod.CONFIG.unsafeLevelAccess.get().booleanValue()) {
            return;
        }
        long currentTick = clientLevel.getGameTime();
        BlockPos origin = LevelAccessUtils.levelOriginFromPlayer();
        CachingClientLevel cachingClientLevel = (CachingClientLevel)clientLevel;
        ClonedClientLevel clientLevelClone = cachingClientLevel.sound_physics_remastered$getCachedClone();
        if (clientLevelClone == null) {
            Loggers.logDebug("Creating new level cache, no existing level clone found in client cache.", new Object[0]);
            LevelAccessUtils.updateLevelCache(clientLevel, origin, SoundPhysicsMod.CONFIG.levelCloneMaxRetainTicks.get().intValue());
            return;
        }
        long ticksSinceLastClone = currentTick - clientLevelClone.getTick();
        double distanceSinceLastClone = origin.distSqr((Vec3i)clientLevelClone.getOrigin());
        if (ticksSinceLastClone >= (long)SoundPhysicsMod.CONFIG.levelCloneMaxRetainTicks.get().intValue() || distanceSinceLastClone >= (double)SoundPhysicsMod.CONFIG.levelCloneMaxRetainBlockDistance.get().intValue()) {
            Loggers.logDebug("Updating level cache, cache expired ({}/{} ticks) or player moved too far ({}/{} block(s)) from last clone origin.", ticksSinceLastClone, SoundPhysicsMod.CONFIG.levelCloneMaxRetainTicks.get(), distanceSinceLastClone, SoundPhysicsMod.CONFIG.levelCloneMaxRetainBlockDistance.get());
            LevelAccessUtils.updateLevelCache(clientLevel, origin, currentTick);
        }
    }

    private static void updateLevelCache(ClientLevel clientLevel, BlockPos origin, long tick) {
        Loggers.logDebug("Updating level cache, creating new level clone with origin {} on tick {}.", origin.toShortString(), tick);
        TaskProfiler.TaskProfilerHandle profile = PROFILER.profile();
        CachingClientLevel cachingClientLevel = (CachingClientLevel)clientLevel;
        ClonedClientLevel clientLevelClone = new ClonedClientLevel(clientLevel, origin, tick, SoundPhysicsMod.CONFIG.levelCloneRange.get());
        cachingClientLevel.sound_physics_remastered$setCachedClone(clientLevelClone);
        profile.finish();
        Loggers.logProfiling("Updated client level clone in cache in {} ms", profile.getDuration());
        PROFILER.onTally(PROFILER::logResults);
    }

    @Nullable
    public static ClientLevelProxy getClientLevelProxy(Minecraft client) {
        ClientLevel clientLevel = client.level;
        if (clientLevel == null) {
            Loggers.warn("Can not return client level proxy, client level does not exist.", new Object[0]);
            return null;
        }
        if (SoundPhysicsMod.CONFIG.unsafeLevelAccess.get().booleanValue()) {
            return new UnsafeClientLevel(clientLevel);
        }
        CachingClientLevel cachingClientLevel = (CachingClientLevel)clientLevel;
        ClonedClientLevel clientLevelClone = cachingClientLevel.sound_physics_remastered$getCachedClone();
        if (clientLevelClone == null) {
            Loggers.warn("Can not return client level proxy, client level clone has not been cached. This might only occur once on load.", new Object[0]);
            return null;
        }
        return clientLevelClone;
    }

    private static BlockPos levelOriginFromPlayer() {
        Vec3 playerPos = LevelAccessUtils.MC.player.position();
        return BlockPos.containing((Position)playerPos);
    }
}

