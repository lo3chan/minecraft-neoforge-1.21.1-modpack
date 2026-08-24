package fuzs.eternalnether.data.tags;

import fuzs.eternalnether.init.ModBlocks;
import fuzs.eternalnether.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class ModBlockTagProvider extends AbstractTagProvider<Block> {
   public ModBlockTagProvider(DataProviderContext context) {
      super(Registries.BLOCK, context);
   }

   public void addTags(Provider provider) {
      this.add(ModRegistry.WITHERED_BLOCK_TAG_KEY)
         .add(
            new Block[]{
               (Block)ModBlocks.WITHERED_BLACKSTONE.value(),
               (Block)ModBlocks.WITHERED_BLACKSTONE_STAIRS.value(),
               (Block)ModBlocks.WITHERED_BLACKSTONE_SLAB.value(),
               (Block)ModBlocks.WITHERED_BLACKSTONE_WALL.value(),
               (Block)ModBlocks.CHISELED_WITHERED_BLACKSTONE.value(),
               (Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE.value(),
               (Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_STAIRS.value(),
               (Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_SLAB.value(),
               (Block)ModBlocks.CRACKED_WITHERED_BLACKSTONE_WALL.value(),
               (Block)ModBlocks.WITHERED_BASALT.value(),
               (Block)ModBlocks.WITHERED_COAL_BLOCK.value(),
               (Block)ModBlocks.WITHERED_QUARTZ_BLOCK.value(),
               (Block)ModBlocks.WITHERED_DEBRIS.value()
            }
         );
      this.add(BlockTags.MINEABLE_WITH_PICKAXE)
         .add(
            new Block[]{
               (Block)ModBlocks.COBBLED_BLACKSTONE.value(),
               (Block)ModBlocks.SOUL_STONE.value(),
               (Block)ModBlocks.WITHERED_BONE_BLOCK.value(),
               (Block)ModBlocks.WARPED_NETHER_BRICKS.value(),
               (Block)ModBlocks.WARPED_NETHER_BRICK_STAIRS.value(),
               (Block)ModBlocks.WARPED_NETHER_BRICK_SLAB.value(),
               (Block)ModBlocks.CHISELED_WARPED_NETHER_BRICKS.value()
            }
         )
         .addTag(ModRegistry.WITHERED_BLOCK_TAG_KEY);
      this.add(BlockTags.NEEDS_DIAMOND_TOOL).addTag(ModRegistry.WITHERED_BLOCK_TAG_KEY);
      this.add(BlockTags.WITHER_SUMMON_BASE_BLOCKS).add(ModBlocks.SOUL_STONE);
   }
}
