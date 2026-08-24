package com.yungnickyoung.minecraft.yungsbridges.world.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class FenceBiomeProcessor implements ITemplateFeatureProcessor {
   @Override
   public void processTemplate(
      StructureTemplate template,
      WorldGenLevel level,
      RandomSource randomSource,
      BlockPos cornerPos,
      BlockPos centerPos,
      StructurePlaceSettings placementSettings
   ) {
      Holder<Biome> biome = level.getBiome(cornerPos);

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.OAK_FENCE)) {
         if (!(randomSource.nextFloat() < 0.75F) && !level.getBlockState(blockInfo.pos().above()).canOcclude()) {
            level.setBlock(blockInfo.pos(), Blocks.AIR.defaultBlockState(), 2);
            BlockPos neighborPos = blockInfo.pos().relative(Direction.NORTH);
            if (level.getBlockState(neighborPos).hasProperty(FenceBlock.SOUTH) && (Boolean)level.getBlockState(neighborPos).getValue(FenceBlock.SOUTH)) {
               level.setBlock(neighborPos, (BlockState)level.getBlockState(neighborPos).setValue(FenceBlock.SOUTH, false), 2);
            }

            neighborPos = blockInfo.pos().relative(Direction.WEST);
            if (level.getBlockState(neighborPos).hasProperty(FenceBlock.EAST) && (Boolean)level.getBlockState(neighborPos).getValue(FenceBlock.EAST)) {
               level.setBlock(neighborPos, (BlockState)level.getBlockState(neighborPos).setValue(FenceBlock.EAST, false), 2);
            }
         } else {
            BlockState fenceBlock = this.getFenceBlockWithState(this.getFenceBiomeVariant(biome), blockInfo.state());
            if (!level.getBlockState(blockInfo.pos().relative(Direction.NORTH)).canOcclude()) {
               fenceBlock = (BlockState)fenceBlock.setValue(FenceBlock.NORTH, false);
            }

            if (!level.getBlockState(blockInfo.pos().relative(Direction.WEST)).canOcclude()) {
               fenceBlock = (BlockState)fenceBlock.setValue(FenceBlock.WEST, false);
            }

            level.setBlock(blockInfo.pos(), fenceBlock, 2);
         }
      }
   }
}
