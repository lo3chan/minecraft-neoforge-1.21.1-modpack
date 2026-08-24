package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.mods.kubejs.recipe.special.ShapelessKubeJSRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface KubeJSRecipeSerializers {
   DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "kubejs");
   Supplier<RecipeSerializer<?>> SHAPED = REGISTRY.register("shaped", ShapedKubeJSRecipe.SerializerKJS::new);
   Supplier<RecipeSerializer<?>> SHAPELESS = REGISTRY.register("shapeless", ShapelessKubeJSRecipe.SerializerKJS::new);
}
