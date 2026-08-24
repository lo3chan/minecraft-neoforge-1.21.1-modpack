package com.mcwwindows.kikoz.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundsInit {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "mcwwindows");
   public static final DeferredHolder<SoundEvent, SoundEvent> BARS_CLOSE = SOUNDS.register(
      "block.bars_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwwindows", "block.bars_close"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BARS_OPEN = SOUNDS.register(
      "block.bars_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwwindows", "block.bars_open"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BLINDS_CLOSE = SOUNDS.register(
      "block.blinds_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwwindows", "block.blinds_close"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BLINDS_OPEN = SOUNDS.register(
      "block.blinds_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwwindows", "block.blinds_open"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WINDOW_CLOSE = SOUNDS.register(
      "block.window_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwwindows", "block.window_close"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WINDOW_OPEN = SOUNDS.register(
      "block.window_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mcwwindows", "block.window_open"))
   );
}
