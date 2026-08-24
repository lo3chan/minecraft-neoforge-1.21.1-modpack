package net.astralya.hexalia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record MortarAndPestleRecipeInput(ItemStack first, ItemStack second, ItemStack third) implements RecipeInput {
   public ItemStack getItem(int index) {
      return switch (index) {
         case 0 -> this.first;
         case 1 -> this.second;
         case 2 -> this.third;
         default -> ItemStack.EMPTY;
      };
   }

   public int size() {
      return 3;
   }
}
