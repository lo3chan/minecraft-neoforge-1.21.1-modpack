package com.aetherteam.aether.api.registers;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.sounds.SoundEvent;

public record AdvancementSoundOverride(int priority, Predicate<AdvancementHolder> predicate, Supplier<SoundEvent> sound) {
   public boolean matches(AdvancementHolder advancement) {
      return this.predicate.test(advancement);
   }
}
