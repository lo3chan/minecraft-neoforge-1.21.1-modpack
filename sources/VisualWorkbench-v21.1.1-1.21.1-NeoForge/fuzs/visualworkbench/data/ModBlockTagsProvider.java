package fuzs.visualworkbench.data;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagsProvider extends AbstractTagProvider<Block> {
   public ModBlockTagsProvider(DataProviderContext context) {
      super(Registries.BLOCK, context);
   }

   public void addTags(Provider provider) {
      this.add(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG).add(new Block[]{Blocks.SMITHING_TABLE, Blocks.FLETCHING_TABLE});
   }
}
