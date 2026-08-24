package com.mcwfurnitures.kikoz.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundsInit {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "mcwfurnitures");
   public static final DeferredHolder<SoundEvent, SoundEvent> DRAWER_OPEN = SOUNDS.register(
      "block.drawer_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwfurnitures", "block.drawer_open"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> DRAWER_CLOSE = SOUNDS.register(
      "block.drawer_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwfurnitures", "block.drawer_close"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CABINET_OPEN = SOUNDS.register(
      "block.cabinet_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwfurnitures", "block.cabinet_open"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> CABINET_CLOSE = SOUNDS.register(
      "block.cabinet_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwfurnitures", "block.cabinet_close"))
   );
}
