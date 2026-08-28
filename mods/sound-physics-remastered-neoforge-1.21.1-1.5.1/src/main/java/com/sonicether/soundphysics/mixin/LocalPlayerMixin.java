/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LocalPlayer.class})
public abstract class LocalPlayerMixin
extends Entity {
    public LocalPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method={"playSound"}, at={@At(value="HEAD")}, cancellable=true)
    private void playSound(SoundEvent soundEvent, float volume, float pitch, CallbackInfo ci) {
        if (!SoundPhysicsMod.CONFIG.enabled.get().booleanValue()) {
            return;
        }
        ci.cancel();
        this.level().playLocalSound((Entity)this, soundEvent, this.getSoundSource(), volume, pitch);
    }
}

