package net.mehvahdjukaar.amendments.common.platform;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;

public class FlowerPotHandlerImpl {
   private static Map<Block, Map<ResourceLocation, Supplier<? extends Block>>> FULL_POTS;

   public static Block getFullPot(FlowerPotBlock emptyPot, Block flowerBlock) {
      return FULL_POTS.get(emptyPot.getEmptyPot()).getOrDefault(Utils.getID(flowerBlock), () -> Blocks.AIR).get();
   }

   public static boolean isEmptyPot(Block b) {
      return FULL_POTS != null && b != null && FULL_POTS.containsKey(b);
   }

   public static void setup() {
      Set<FlowerPotBlock> emptyPots = new HashSet<>();

      for (Block b : BuiltInRegistries.BLOCK) {
         if (b instanceof FlowerPotBlock flowerPotBlock) {
            emptyPots.add(flowerPotBlock.getEmptyPot());
         }
      }

      FULL_POTS = new IdentityHashMap<>();

      for (FlowerPotBlock pot : emptyPots) {
         FULL_POTS.put(pot, pot.getFullPotsView());
      }
   }

   public static Block getEmptyPot(FlowerPotBlock fullPot) {
      return fullPot.getEmptyPot();
   }
}
