package io.github.razordevs.deep_aether.block.misc;

import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class BurnableBlockItem extends BlockItem {
   private final int burnTime;

   public BurnableBlockItem(int burnTime, Block block, Properties properties) {
      super(block, properties);
      this.burnTime = burnTime;
   }

   public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
      return this.burnTime;
   }
}
