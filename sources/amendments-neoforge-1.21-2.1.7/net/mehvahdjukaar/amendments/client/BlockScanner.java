package net.mehvahdjukaar.amendments.client;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Set;
import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.FarmersDelightCompat;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import org.jetbrains.annotations.NotNull;

public class BlockScanner {
   private static BlockScanner INSTANCE = null;
   private final Set<Block> torches;
   private final Set<Block> candleHolders;
   private final Set<Block> fdSigns;

   public static BlockScanner getInstance() {
      if (INSTANCE == null) {
         Amendments.LOGGER.debug("Scanning blocks for Amendments");
         INSTANCE = new BlockScanner();
      }

      return INSTANCE;
   }

   private BlockScanner() {
      Builder<Block> torchesBuilder = ImmutableSet.builder();
      Builder<Block> candleBuilder = ImmutableSet.builder();
      Builder<Block> fdSignsBuilder = ImmutableSet.builder();

      for (Block block : BuiltInRegistries.BLOCK) {
         if ((!(block instanceof TorchBlock) || block instanceof WallTorchBlock) && (!CompatHandler.SUPPLEMENTARIES || !SuppCompat.isSconce(block))) {
            if (CompatHandler.SUPPLEMENTARIES && SuppCompat.isCandleHolder(block)) {
               candleBuilder.add(block);
            } else if (CompatHandler.FARMERS_DELIGHT && FarmersDelightCompat.isStandingSign(block)) {
               fdSignsBuilder.add(block);
            }
         } else {
            torchesBuilder.add(block);
         }
      }

      torchesBuilder.add(Blocks.REDSTONE_TORCH);
      this.torches = torchesBuilder.build();
      this.candleHolders = candleBuilder.build();
      this.fdSigns = fdSignsBuilder.build();
   }

   @NotNull
   public Set<Block> getTorches() {
      return this.torches;
   }

   @NotNull
   public Set<Block> getCandleHolders() {
      return this.candleHolders;
   }

   public Set<Block> getFdSigns() {
      return this.fdSigns;
   }
}
