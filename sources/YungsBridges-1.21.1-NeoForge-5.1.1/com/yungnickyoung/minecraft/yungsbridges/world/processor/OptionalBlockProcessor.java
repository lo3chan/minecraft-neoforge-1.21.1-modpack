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

public class OptionalBlockProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer brownStainedGlassReplacer = new BlockStateRandomizer(AIR)
      .addBlock(Blocks.STONE_BRICKS.defaultBlockState(), 0.1875F)
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.375F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.1875F);
   private final BlockStateRandomizer blueStainedGlassReplacer = new BlockStateRandomizer(AIR)
      .addBlock(Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 0.75F);
   private final BlockStateRandomizer redStainedGlassReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.COBBLESTONE.defaultBlockState(), 0.75F);
   private final BlockStateRandomizer endStoneBricksReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.POLISHED_ANDESITE.defaultBlockState(), 0.75F);
   private final BlockStateRandomizer darkPrismarineReplacer = new BlockStateRandomizer(AIR)
      .addBlock(Blocks.COBBLESTONE.defaultBlockState(), 0.375F)
      .addBlock(Blocks.ANDESITE.defaultBlockState(), 0.375F);

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

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.BROWN_STAINED_GLASS)) {
         level.setBlock(blockInfo.pos(), this.brownStainedGlassReplacer.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.BLUE_STAINED_GLASS)) {
         level.setBlock(blockInfo.pos(), this.blueStainedGlassReplacer.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.RED_STAINED_GLASS)) {
         level.setBlock(blockInfo.pos(), this.redStainedGlassReplacer.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.END_STONE_BRICKS)) {
         level.setBlock(blockInfo.pos(), this.endStoneBricksReplacer.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.DARK_PRISMARINE)) {
         level.setBlock(blockInfo.pos(), this.darkPrismarineReplacer.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.LIME_STAINED_GLASS)) {
         if (randomSource.nextFloat() < 0.5F) {
            level.setBlock(blockInfo.pos(), this.getLogBlockWithState(this.getLogBiomeVariant(biome), blockInfo.state()), 2);
         } else {
            level.setBlock(blockInfo.pos(), Blocks.AIR.defaultBlockState(), 2);
         }
      }
   }
}
