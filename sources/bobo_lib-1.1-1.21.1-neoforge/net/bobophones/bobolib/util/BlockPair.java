package net.bobophones.bobolib.util;

import net.minecraft.world.level.block.Block;

public record BlockPair(Block block0, Block block1) {
   public BlockPair(Block block0, Block block1) {
      if (block0.getName().getString().compareTo(block1.getName().getString()) <= 0) {
         this.block0 = block0;
         this.block1 = block1;
      } else {
         this.block0 = block1;
         this.block1 = block0;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BlockPair other) ? false : this.block0.equals(other.block0) && this.block1.equals(other.block1);
      }
   }
}
