package net.blay09.mods.balm.neoforge.client.renderer.chunk.internal;

import net.blay09.mods.balm.client.renderer.chunk.BalmBlockRenderTypeRegistrar;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public class NeoForgeBalmBlockRenderTypeRegistrar implements BalmBlockRenderTypeRegistrar {
   @Override
   public void setRenderLayer(Holder<Block> block, RenderType layer) {
      ItemBlockRenderTypes.setRenderLayer((Block)block.value(), layer);
   }
}
