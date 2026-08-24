package com.seibel.distanthorizons.common.wrappers.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.LeavesBlock;

enum ClientBlockStateColorCache$EColorMode_neoforge {
   Default,
   Flower,
   Leaves,
   Chisel,
   Glass;

   static ClientBlockStateColorCache$EColorMode_neoforge getColorMode(Block block) {
      boolean isLeavesBlock = block instanceof LeavesBlock;
      if (isLeavesBlock) {
         return Leaves;
      } else {
         boolean isFlowerBlock = block instanceof FlowerBlock;
         if (isFlowerBlock) {
            return Flower;
         } else if (block.toString().contains("glass")) {
            return Glass;
         } else {
            return block.toString().equals("Block{chiselsandbits:chiseled}") ? Chisel : Default;
         }
      }
   }
}
