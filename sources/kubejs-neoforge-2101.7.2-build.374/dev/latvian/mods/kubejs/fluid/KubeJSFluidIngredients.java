package dev.latvian.mods.kubejs.fluid;

import java.util.function.Supplier;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public interface KubeJSFluidIngredients {
   DeferredRegister<FluidIngredientType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.FLUID_INGREDIENT_TYPES, "kubejs");
   Supplier<FluidIngredientType<?>> REGEX = REGISTRY.register(
      "regex", () -> new FluidIngredientType(RegExFluidIngredient.CODEC, RegExFluidIngredient.STREAM_CODEC)
   );
   Supplier<FluidIngredientType<?>> NAMESPACE = REGISTRY.register(
      "namespace", () -> new FluidIngredientType(NamespaceFluidIngredient.CODEC, NamespaceFluidIngredient.STREAM_CODEC)
   );
}
