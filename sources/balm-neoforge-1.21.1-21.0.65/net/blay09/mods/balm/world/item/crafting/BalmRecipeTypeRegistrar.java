package net.blay09.mods.balm.world.item.crafting;

import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public interface BalmRecipeTypeRegistrar {
   default <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> BalmRecipeTypeRegistration<TRecipeInput, TRecipe> register(
      String name, Class<TRecipe> recipeClass
   ) {
      return this.register(name, SimpleRecipeType.of(recipeClass));
   }

   <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> BalmRecipeTypeRegistration<TRecipeInput, TRecipe> register(
      String var1, Function<ResourceLocation, ? extends RecipeType<TRecipe>> var2
   );

   <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> BalmRecipeSerializerRegistration<TRecipe> registerSerializer(
      String var1, Function<ResourceLocation, RecipeSerializer<TRecipe>> var2
   );
}
