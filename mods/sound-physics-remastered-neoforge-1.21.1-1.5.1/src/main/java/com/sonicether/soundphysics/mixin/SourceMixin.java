/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.Channel
 *  net.minecraft.world.phys.Vec3
 *  org.lwjgl.openal.AL10
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sonicether.soundphysics.mixin;

import com.mojang.blaze3d.audio.Channel;
import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Channel.class})
public class SourceMixin {
    @Shadow
    @Final
    private int source;
    private Vec3 pos;

    @Inject(method={"setSelfPosition"}, at={@At(value="HEAD")})
    private void setSelfPosition(Vec3 poss, CallbackInfo ci) {
        this.pos = poss;
    }

    @Inject(method={"play"}, at={@At(value="HEAD")})
    private void play(CallbackInfo ci) {
        SoundPhysics.onPlaySound(this.pos.x, this.pos.y, this.pos.z, this.source);
        Loggers.logALError("Sound play injector");
    }

    @ModifyVariable(method={"linearAttenuation"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private float injected(float attenuation) {
        if (!SoundPhysicsMod.CONFIG.enabled.get().booleanValue()) {
            return attenuation;
        }
        return attenuation / SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue();
    }

    @Inject(method={"linearAttenuation"}, at={@At(value="RETURN")})
    private void linearAttenuation2(float attenuation, CallbackInfo ci) {
        AL10.alSourcef((int)this.source, (int)4128, (float)(attenuation / 2.0f));
    }
}

