package net.blay09.mods.balm.api.sound;

import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@Deprecated
public interface BalmSounds {
   @Deprecated
   DeferredObject<SoundEvent> register(ResourceLocation var1);
}
