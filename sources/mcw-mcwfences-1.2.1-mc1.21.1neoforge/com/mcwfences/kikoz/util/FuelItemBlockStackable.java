package com.mcwfences.kikoz.util;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class FuelItemBlockStackable extends StackableTooltip {
   public FuelItemBlockStackable(Block block, Properties prop) {
      super(block, prop);
   }

   public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
      return 300;
   }
}
