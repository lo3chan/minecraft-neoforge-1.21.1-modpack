package net.bettercombat.api.component;

import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class BetterCombatDataComponents {
   public static final DataComponentType<ResourceLocation> WEAPON_PRESET_ID = register(
      ResourceLocation.fromNamespaceAndPath("bettercombat", "preset_id"), builder -> builder.persistent(ResourceLocation.CODEC)
   );

   private static <T> DataComponentType<T> register(ResourceLocation id, UnaryOperator<Builder<T>> builderOperator) {
      return (DataComponentType<T>)Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builderOperator.apply(DataComponentType.builder()).build());
   }
}
