package com.github.alexthe666.citadel.item;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class BlockItemWithSupplier extends BlockItem {
   private final Supplier<Block> blockSupplier;

   public BlockItemWithSupplier(Supplier<Block> blockSupplier, Properties props) {
      super(null, props);
      this.blockSupplier = blockSupplier;
   }

   public Block getBlock() {
      return this.blockSupplier.get();
   }

   public boolean canFitInsideContainerItems() {
      return !(this.blockSupplier.get() instanceof ShulkerBoxBlock);
   }
}
