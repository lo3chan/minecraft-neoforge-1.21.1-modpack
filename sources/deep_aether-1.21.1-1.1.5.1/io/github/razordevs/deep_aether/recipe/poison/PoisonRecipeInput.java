package io.github.razordevs.deep_aether.recipe.poison;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record PoisonRecipeInput(ItemStack item) implements RecipeInput {
   public ItemStack getItem(int index) {
      if (index == 0) {
         return this.item;
      } else {
         throw new IllegalArgumentException("Recipe does not contain slot " + index);
      }
   }

   public int size() {
      return 1;
   }
}
