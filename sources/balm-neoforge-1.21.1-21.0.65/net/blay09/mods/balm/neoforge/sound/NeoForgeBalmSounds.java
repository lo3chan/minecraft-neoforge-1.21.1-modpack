package net.blay09.mods.balm.neoforge.sound;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.sound.BalmSounds;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgeBalmSounds implements BalmSounds {
   @Override
   public DeferredObject<SoundEvent> register(ResourceLocation identifier) {
      DeferredRegister<SoundEvent> register = DeferredRegisters.get(Registries.SOUND_EVENT, identifier.getNamespace());
      DeferredHolder<SoundEvent, SoundEvent> registryObject = register.register(identifier.getPath(), () -> SoundEvent.createVariableRangeEvent(identifier));
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }
}
