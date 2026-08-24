package com.github.alexthe666.alexsmobs.effect;

import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

public class ProperBrewingRecipe extends BrewingRecipe {
   private final ItemStack input;

   public ProperBrewingRecipe(ItemStack input, Ingredient ingredient, ItemStack output) {
      super(Ingredient.of(new ItemLike[]{input.getItem()}), ingredient, output);
      this.input = input;
   }

   public boolean isInput(@Nonnull ItemStack stack) {
      return stack == null ? false : ItemStack.isSameItem(stack, this.input) && ItemStack.isSameItemSameComponents(this.input, stack);
   }
}
