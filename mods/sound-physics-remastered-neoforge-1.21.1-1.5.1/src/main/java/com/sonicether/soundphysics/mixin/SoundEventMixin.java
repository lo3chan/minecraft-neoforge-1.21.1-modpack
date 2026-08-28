/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 */
package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value={SoundEvent.class})
public class SoundEventMixin {
    @ModifyConstant(method={"getRange"}, constant={@Constant(floatValue=16.0f)}, expect=2)
    private float allowance1(float value) {
        if (!SoundPhysicsMod.CONFIG.enabled.get().booleanValue()) {
            return value;
        }
        return value * SoundPhysicsMod.CONFIG.soundDistanceAllowance.get().floatValue();
    }
}

