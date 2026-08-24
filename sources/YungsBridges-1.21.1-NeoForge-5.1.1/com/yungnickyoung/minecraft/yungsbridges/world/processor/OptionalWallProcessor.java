package com.yungnickyoung.minecraft.yungsbridges.world.processor;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class OptionalWallProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer endStoneBrickWallReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.STONE_BRICK_WALL.defaultBlockState(), 0.5F);

   @Override
   public void processTemplate(
      StructureTemplate template,
      WorldGenLevel level,
      RandomSource randomSource,
      BlockPos cornerPos,
      BlockPos centerPos,
      StructurePlaceSettings placementSettings
   ) {
      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.END_STONE_BRICK_WALL)) {
         level.setBlock(blockInfo.pos(), this.getWallBlockWithState(this.endStoneBrickWallReplacer.get(randomSource), blockInfo.state()), 2);
      }
   }
}
