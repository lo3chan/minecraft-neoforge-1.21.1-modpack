package net.mehvahdjukaar.amendments.common;

import net.mehvahdjukaar.amendments.common.platform.FlowerPotHandlerImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

public class FlowerPotHandler {
   public static Block getEmptyPot(FlowerPotBlock var0) {
      return FlowerPotHandlerImpl.getEmptyPot(var0);
   }

   public static Block getFullPot(FlowerPotBlock var0, Block var1) {
      return FlowerPotHandlerImpl.getFullPot(var0, var1);
   }

   public static boolean isEmptyPot(Block var0) {
      return FlowerPotHandlerImpl.isEmptyPot(var0);
   }

   public static void setup() {
      FlowerPotHandlerImpl.setup();
   }
}
