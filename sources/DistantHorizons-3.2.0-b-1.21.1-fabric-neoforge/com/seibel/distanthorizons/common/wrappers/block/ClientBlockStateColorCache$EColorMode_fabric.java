package com.seibel.distanthorizons.common.wrappers.block;

import net.minecraft.class_2248;
import net.minecraft.class_2356;
import net.minecraft.class_2397;

enum ClientBlockStateColorCache$EColorMode_fabric {
   Default,
   Flower,
   Leaves,
   Chisel,
   Glass;

   static ClientBlockStateColorCache$EColorMode_fabric getColorMode(class_2248 block) {
      boolean isLeavesBlock = block instanceof class_2397;
      if (isLeavesBlock) {
         return Leaves;
      } else {
         boolean isFlowerBlock = block instanceof class_2356;
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
