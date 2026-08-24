package net.blay09.mods.balm.api.compat.hudinfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface BalmModSupportHudInfo {
   void registerGlobalBlockInfo(ResourceLocation var1, BlockInfoProvider var2);

   void registerBlockInfo(ResourceLocation var1, Block var2, BlockInfoProvider var3);
}
