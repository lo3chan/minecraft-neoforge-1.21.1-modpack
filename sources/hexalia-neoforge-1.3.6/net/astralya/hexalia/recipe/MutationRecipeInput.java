package net.astralya.hexalia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record MutationRecipeInput(ItemStack input) implements RecipeInput {
   public ItemStack getItem(int index) {
      return this.input;
   }

   public int size() {
      return 1;
   }
}
