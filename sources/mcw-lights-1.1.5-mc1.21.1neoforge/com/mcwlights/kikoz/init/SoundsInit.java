package com.mcwlights.kikoz.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundsInit {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "mcwlights");
   public static final DeferredHolder<SoundEvent, SoundEvent> LIGHT_SWITCH = SOUNDS.register(
      "block.light_switch", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwlights", "block.light_switch"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> TORCH_ON = SOUNDS.register(
      "block.torch_on", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwlights", "block.torch_on"))
   );
}
