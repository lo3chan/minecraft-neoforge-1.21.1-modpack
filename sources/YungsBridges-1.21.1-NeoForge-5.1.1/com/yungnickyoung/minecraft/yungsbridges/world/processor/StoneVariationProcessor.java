package com.yungnickyoung.minecraft.yungsbridges.world.processor;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class StoneVariationProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer stoneBrickSelector = new BlockStateRandomizer(Blocks.STONE_BRICKS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.5F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.25F);
   private final BlockStateRandomizer stoneBrickSlabSelector = new BlockStateRandomizer(Blocks.STONE_BRICK_SLAB.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState(), 0.5F);
   private final BlockStateRandomizer stoneBrickWallSelector = new BlockStateRandomizer(Blocks.STONE_BRICK_WALL.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState(), 0.5F);
   private final BlockStateRandomizer stoneBrickStairSelector = new BlockStateRandomizer(Blocks.STONE_BRICK_STAIRS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), 0.5F);
   private final BlockStateRandomizer cobblestoneSelector = new BlockStateRandomizer(Blocks.COBBLESTONE.defaultBlockState())
      .addBlock(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 0.6F);

   @Override
   public void processTemplate(
      StructureTemplate template,
      WorldGenLevel level,
      RandomSource randomSource,
      BlockPos cornerPos,
      BlockPos centerPos,
      StructurePlaceSettings placementSettings
   ) {
      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.STONE_BRICKS)) {
         level.setBlock(blockInfo.pos(), this.stoneBrickSelector.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.STONE_BRICK_SLAB)) {
         level.setBlock(blockInfo.pos(), this.getSlabBlockWithState(this.stoneBrickSlabSelector.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.STONE_BRICK_WALL)) {
         level.setBlock(blockInfo.pos(), this.getWallBlockWithState(this.stoneBrickWallSelector.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.STONE_BRICK_STAIRS)) {
         level.setBlock(blockInfo.pos(), this.getStairsBlockWithState(this.stoneBrickStairSelector.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.COBBLESTONE)) {
         level.setBlock(blockInfo.pos(), this.cobblestoneSelector.get(randomSource), 2);
      }
   }
}
