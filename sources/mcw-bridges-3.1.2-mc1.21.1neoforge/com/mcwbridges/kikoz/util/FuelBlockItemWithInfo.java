package com.mcwbridges.kikoz.util;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class FuelBlockItemWithInfo extends BlockItemWithInfo {
   public FuelBlockItemWithInfo(Block block, Properties prop) {
      super(block, prop);
   }

   public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
      return 300;
   }
}
