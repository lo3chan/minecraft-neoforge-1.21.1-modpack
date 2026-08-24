package dev.architectury.hooks.item.food.forge;

import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties.Builder;

public class FoodPropertiesHooksImpl {
   public static void effect(Builder builder, Supplier<? extends MobEffectInstance> effectSupplier, float chance) {
      builder.effect(effectSupplier, chance);
   }
}
