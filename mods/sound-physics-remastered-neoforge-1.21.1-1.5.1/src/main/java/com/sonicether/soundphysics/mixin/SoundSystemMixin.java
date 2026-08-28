/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.ChannelAccess$ChannelHandle
 *  net.minecraft.client.sounds.SoundEngine
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package com.sonicether.soundphysics.mixin;

import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.mixin.ChannelAccessor;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={SoundEngine.class})
public class SoundSystemMixin {
    private final Minecraft minecraft = Minecraft.getInstance();

    @Inject(method={"loadLibrary"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/audio/Listener;reset()V")})
    private void loadLibrary(CallbackInfo ci) {
        SoundPhysics.init();
    }

    @Inject(method={"play"}, at={@At(value="FIELD", target="Lnet/minecraft/client/sounds/SoundEngine;instanceBySource:Lcom/google/common/collect/Multimap;")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void play(SoundInstance sound, CallbackInfo ci, WeighedSoundEvents weightedSoundSet, ResourceLocation identifier, Sound sound2, float f, float g, SoundSource soundCategory) {
        SoundPhysics.setLastSoundCategoryAndName(soundCategory, sound.getLocation());
    }

    @Inject(method={"tickNonPaused"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Options;getSoundSourceVolume(Lnet/minecraft/sounds/SoundSource;)F")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void tickNonPaused(CallbackInfo ci, Iterator<?> iterator, Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> map, ChannelAccess.ChannelHandle channelHandle, SoundInstance sound) {
        if (!SoundPhysicsMod.CONFIG.updateMovingSounds.get().booleanValue()) {
            return;
        }
        if (this.minecraft.level != null && (this.minecraft.level.getGameTime() + (long)sound.hashCode()) % (long)SoundPhysicsMod.CONFIG.soundUpdateInterval.get().intValue() == 0L) {
            channelHandle.execute(channel -> SoundPhysics.processSound(((ChannelAccessor)channel).getSource(), sound.getX(), sound.getY(), sound.getZ(), sound.getSource(), sound.getLocation()));
        }
    }
}

