package net.blay09.mods.balm.client.color.block;

import java.util.function.Supplier;
import net.blay09.mods.balm.world.level.block.BlockLike;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

public interface BalmBlockColorRegistrar {
   void register(BlockColor var1, BlockLike... var2);

   void register(BlockColor var1, Iterable<? extends BlockLike> var2);

   void register(BlockColor var1, Supplier<Block[]> var2);
}
