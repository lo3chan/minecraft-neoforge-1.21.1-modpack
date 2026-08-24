package net.blay09.mods.balm.world.item.crafting;

import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public class SimpleRecipeType<T extends Recipe<?>> implements RecipeType<T> {
   private final ResourceLocation identifier;

   public SimpleRecipeType(ResourceLocation identifier) {
      this.identifier = identifier;
   }

   @Override
   public String toString() {
      return this.identifier.getPath();
   }

   public static <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> Function<ResourceLocation, SimpleRecipeType<TRecipe>> of(
      Class<TRecipe> clazz
   ) {
      return SimpleRecipeType::new;
   }
}
