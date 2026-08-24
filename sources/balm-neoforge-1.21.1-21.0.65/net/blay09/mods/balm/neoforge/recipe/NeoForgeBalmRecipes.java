package net.blay09.mods.balm.neoforge.recipe;

import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.recipe.BalmRecipes;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgeBalmRecipes implements BalmRecipes {
   @Override
   public <T extends Recipe<?>> DeferredObject<RecipeType<T>> registerRecipeType(
      Function<ResourceLocation, RecipeType<T>> supplier, ResourceLocation identifier
   ) {
      DeferredRegister<RecipeType<?>> register = DeferredRegisters.get(Registries.RECIPE_TYPE, identifier.getNamespace());
      DeferredHolder<RecipeType<?>, RecipeType<T>> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public <T extends Recipe<?>> DeferredObject<RecipeSerializer<T>> registerRecipeSerializer(
      Supplier<RecipeSerializer<T>> supplier, ResourceLocation identifier
   ) {
      DeferredRegister<RecipeSerializer<?>> register = DeferredRegisters.get(Registries.RECIPE_SERIALIZER, identifier.getNamespace());
      DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }
}
