package dev.architectury.mixin.inject;

import dev.architectury.extensions.injected.InjectedFoodPropertiesBuilderExtension;
import net.minecraft.world.food.FoodProperties.Builder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Builder.class})
public class MixinFoodPropertiesBuilder implements InjectedFoodPropertiesBuilderExtension {
}
