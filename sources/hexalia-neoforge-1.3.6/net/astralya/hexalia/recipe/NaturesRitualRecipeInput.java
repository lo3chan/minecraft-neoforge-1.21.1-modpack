package net.astralya.hexalia.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record NaturesRitualRecipeInput(Container inventory) implements RecipeInput {
   public ItemStack getItem(int index) {
      return this.inventory.getItem(index);
   }

   public int size() {
      return this.inventory.getContainerSize();
   }
}
