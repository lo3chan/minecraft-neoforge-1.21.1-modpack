package net.blay09.mods.balm.client.color.block.internal;

import java.util.ArrayList;
import java.util.Set;
import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.world.level.block.BlockLike;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

public abstract class AbstractBalmBlockColorRegistrar implements BalmBlockColorRegistrar {
   @Override
   public void register(BlockColor color, BlockLike... blocks) {
      this.register(color, Set.of(blocks));
   }

   @Override
   public void register(BlockColor color, Iterable<? extends BlockLike> blocks) {
      this.register(color, () -> {
         ArrayList<Block> resolvedBlocks = new ArrayList<>();

         for (BlockLike blockLike : blocks) {
            resolvedBlocks.add(blockLike.asBlock());
         }

         return resolvedBlocks.toArray(Block[]::new);
      });
   }
}
