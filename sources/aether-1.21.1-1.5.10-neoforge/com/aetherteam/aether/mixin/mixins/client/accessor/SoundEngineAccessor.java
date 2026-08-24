package com.aetherteam.aether.mixin.mixins.client.accessor;

import java.util.Map;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.ChannelAccess.ChannelHandle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SoundEngine.class})
public interface SoundEngineAccessor {
   @Accessor("instanceToChannel")
   Map<SoundInstance, ChannelHandle> aether$getInstanceToChannel();
}
