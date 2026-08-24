package net.blay09.mods.balm.client.renderer.chunk;

import net.blay09.mods.balm.world.level.block.BlockLike;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public interface BalmBlockRenderTypeRegistrar {
   default void setRenderLayer(DeferredBlock block, RenderType layer) {
      this.setRenderLayer(block.asHolder(), layer);
   }

   default void setRenderLayer(BlockLike block, RenderType layer) {
      this.setRenderLayer(block.asHolder(), layer);
   }

   void setRenderLayer(Holder<Block> var1, RenderType var2);
}
