package net.blay09.mods.balm.world.item.crafting;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface DeferredRecipeType<TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> {
   RecipeType<TRecipe> type();

   RecipeSerializer<TRecipe> serializer();

   default Optional<RecipeHolder<TRecipe>> getRecipeFor(Level level, TRecipeInput input) {
      return this.getRecipeFor(level, input, (RecipeHolder<TRecipe>)null);
   }

   Optional<RecipeHolder<TRecipe>> getRecipeFor(Level var1, TRecipeInput var2, ResourceKey<Recipe<?>> var3);

   Optional<RecipeHolder<TRecipe>> getRecipeFor(Level var1, TRecipeInput var2, RecipeHolder<TRecipe> var3);
}
