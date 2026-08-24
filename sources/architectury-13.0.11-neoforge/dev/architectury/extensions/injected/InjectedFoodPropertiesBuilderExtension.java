package dev.architectury.extensions.injected;

import dev.architectury.hooks.item.food.FoodPropertiesHooks;
import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties.Builder;

public interface InjectedFoodPropertiesBuilderExtension {
   default Builder arch$effect(Supplier<? extends MobEffectInstance> effectSupplier, float chance) {
      FoodPropertiesHooks.effect((Builder)this, effectSupplier, chance);
      return (Builder)this;
   }
}
