package com.yungnickyoung.minecraft.yungsbridges.world.processor;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class OptionalStairProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer purpurStairsReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 0.75F);
   private final BlockStateRandomizer darkPrismarineStairsReplacer = new BlockStateRandomizer(AIR)
      .addBlock(Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 0.375F)
      .addBlock(Blocks.ANDESITE_STAIRS.defaultBlockState(), 0.375F);
   private final BlockStateRandomizer warpedStairsReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 0.75F);

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

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.PURPUR_STAIRS)) {
         level.setBlock(blockInfo.pos(), this.getStairsBlockWithState(this.purpurStairsReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.DARK_PRISMARINE_STAIRS)) {
         level.setBlock(blockInfo.pos(), this.getStairsBlockWithState(this.darkPrismarineStairsReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.WARPED_STAIRS)) {
         level.setBlock(blockInfo.pos(), this.getStairsBlockWithState(this.warpedStairsReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.CRIMSON_STAIRS)) {
         level.setBlock(blockInfo.pos(), this.getStairsBlockWithState(this.getStairsBiomeVariant(biome), blockInfo.state()), 2);
      }
   }
}
