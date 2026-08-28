/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.utils.LevelAccessUtils;
import com.sonicether.soundphysics.utils.SoundRateManager;
import com.sonicether.soundphysics.world.CachingClientLevel;
import com.sonicether.soundphysics.world.ClonedClientLevel;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientLevel.class})
public abstract class ClientLevelMixin
implements CachingClientLevel {
    @Unique
    private final AtomicReference<ClonedClientLevel> cachedClone = new AtomicReference();

    @Override
    @Unique
    @Nullable
    public ClonedClientLevel sound_physics_remastered$getCachedClone() {
        return this.cachedClone.get();
    }

    @Override
    @Unique
    public void sound_physics_remastered$setCachedClone(@Nullable ClonedClientLevel clonedClientLevel) {
        this.cachedClone.set(clonedClientLevel);
    }

    @Inject(method={"tick(Ljava/util/function/BooleanSupplier;)V"}, at={@At(value="TAIL")})
    private void tick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        LevelAccessUtils.tickLevelCache((ClientLevel)this);
        SoundRateManager.onClientTick((ClientLevel)this);
    }
}

