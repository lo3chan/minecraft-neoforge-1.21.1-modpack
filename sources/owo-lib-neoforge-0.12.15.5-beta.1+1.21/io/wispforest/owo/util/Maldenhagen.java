package io.wispforest.owo.util;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.block.Block;

public final class Maldenhagen {
   private static final Set<Block> COPIUM_INJECTED = new HashSet<>();

   private Maldenhagen() {
   }

   public static void injectCopium(Block block) {
      COPIUM_INJECTED.add(block);
   }

   public static boolean isOnCopium(Block block) {
      return COPIUM_INJECTED.contains(block);
   }
}
