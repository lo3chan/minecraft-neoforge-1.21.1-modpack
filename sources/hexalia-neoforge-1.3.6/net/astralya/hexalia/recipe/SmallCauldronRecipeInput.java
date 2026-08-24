package net.astralya.hexalia.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public final class SmallCauldronRecipeInput implements RecipeInput {
   private final List<ItemStack> items;

   public SmallCauldronRecipeInput(List<ItemStack> stacks) {
      this.items = new ArrayList<>(stacks.size());

      for (ItemStack stack : stacks) {
         this.items.add(stack.copy());
      }
   }

   public ItemStack getItem(int index) {
      return this.items.get(index);
   }

   public int size() {
      return this.items.size();
   }
}
