/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sonicether.soundphysics.world;

import com.sonicether.soundphysics.world.ClonedClientLevel;
import javax.annotation.Nullable;

public interface CachingClientLevel {
    @Nullable
    public ClonedClientLevel sound_physics_remastered$getCachedClone();

    public void sound_physics_remastered$setCachedClone(@Nullable ClonedClientLevel var1);
}

