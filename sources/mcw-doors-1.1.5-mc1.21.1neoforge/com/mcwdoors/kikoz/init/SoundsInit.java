package com.mcwdoors.kikoz.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundsInit {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "mcwdoors");
   public static final DeferredHolder<SoundEvent, SoundEvent> GARAGE = SOUNDS.register(
      "block.garage", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwdoors", "block.garage"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SHOJI = SOUNDS.register(
      "block.shoji", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwdoors", "block.shoji"))
   );
}
