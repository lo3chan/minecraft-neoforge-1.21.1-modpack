package io.github.razordevs.deep_aether.recipe.combiner;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CombinerRecipeInput(List<ItemStack> items) implements RecipeInput {
   public ItemStack getItem(int index) {
      switch (index) {
         case 0:
            return (ItemStack)this.items.getFirst();
         case 1:
            return this.items.get(1);
         case 2:
            return this.items.get(2);
         default:
            throw new IllegalArgumentException("Recipe does not contain slot " + index);
      }
   }

   public int size() {
      return 3;
   }
}
