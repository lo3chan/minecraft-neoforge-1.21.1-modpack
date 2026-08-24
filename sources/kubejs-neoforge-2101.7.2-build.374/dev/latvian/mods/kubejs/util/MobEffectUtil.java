package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MobEffectUtil {
   @Info("Copies an existing MobEffectInstance")
   @Contract("_ -> new")
   @NotNull
   public static MobEffectInstance of(MobEffectInstance oldInstance) {
      return new MobEffectInstance(oldInstance);
   }

   @Info("Creates an instance for the given effect. Default duration and amplifier is 0")
   @Contract("_ -> new")
   @NotNull
   public static MobEffectInstance of(Holder<MobEffect> effect) {
      return new MobEffectInstance(effect);
   }

   @Info("Creates an instance for the given effect and duration (in ticks)")
   @Contract("_, _ -> new")
   @NotNull
   public static MobEffectInstance of(Holder<MobEffect> effect, TickDuration duration) {
      return new MobEffectInstance(effect, duration.intTicks());
   }

   @Info("Creates an instance for the given effect, duration and amplifier")
   @Contract("_, _, _ -> new")
   @NotNull
   public static MobEffectInstance of(Holder<MobEffect> effect, TickDuration duration, int amplifier) {
      return new MobEffectInstance(effect, duration.intTicks(), amplifier);
   }

   @Info("Creates an instance for the given effect, duration, amplifier, ambient, and visible to the HUD")
   @Contract("_, _, _, _, _ -> new")
   @NotNull
   public static MobEffectInstance of(Holder<MobEffect> effect, TickDuration duration, int amplifier, boolean ambient, boolean visible) {
      return new MobEffectInstance(effect, duration.intTicks(), amplifier, ambient, visible);
   }

   @Info("Creates an instance for the given effect, duration, amplifier, ambient, visible to the HUD, and to show the icon on the sceen")
   @Contract("_, _, _, _, _, _ -> new")
   @NotNull
   public static MobEffectInstance of(Holder<MobEffect> effect, TickDuration duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
      return new MobEffectInstance(effect, duration.intTicks(), amplifier, ambient, visible, showIcon);
   }
}
