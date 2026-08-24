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

public class DynamicLegProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer stoneBrickSelector = new BlockStateRandomizer(Blocks.STONE_BRICKS.defaultBlockState())
      .addBlock(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 0.5F)
      .addBlock(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 0.25F);
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
      Holder<Biome> biome = level.getBiome(cornerPos);

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.YELLOW_STAINED_GLASS)) {
         this.generatePillarDown(
            level,
            blockInfo.pos(),
            () -> this.getLogBlockWithState(this.getLogBiomeVariant(biome), blockInfo.state()),
            () -> this.getLogBlockWithState(this.getLogBiomeVariant(biome), blockInfo.state())
         );
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.WARPED_FENCE)) {
         this.generatePillarDown(
            level,
            blockInfo.pos(),
            () -> this.getFenceBlockWithState(this.getFenceBiomeVariant(biome), blockInfo.state()),
            () -> this.getFenceBlockWithState(this.getFenceBiomeVariant(biome), blockInfo.state())
         );
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.PINK_STAINED_GLASS)) {
         this.generatePillarDown(level, blockInfo.pos(), () -> this.stoneBrickSelector.get(randomSource), () -> this.stoneBrickSelector.get(randomSource));
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.LIGHT_BLUE_STAINED_GLASS)) {
         this.generatePillarDown(level, blockInfo.pos(), Blocks.POLISHED_ANDESITE::defaultBlockState, Blocks.POLISHED_ANDESITE::defaultBlockState);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.MAGENTA_STAINED_GLASS)) {
         this.generatePillarDown(level, blockInfo.pos(), () -> this.cobblestoneSelector.get(randomSource), () -> this.cobblestoneSelector.get(randomSource));
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.GRAY_STAINED_GLASS)) {
         this.generatePillarDown(level, blockInfo.pos(), Blocks.POLISHED_ANDESITE::defaultBlockState, () -> this.stoneBrickSelector.get(randomSource));
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.PRISMARINE_WALL)) {
         this.generatePillarDown(
            level,
            blockInfo.pos(),
            () -> this.getWallBlockWithState(Blocks.COBBLESTONE_WALL.defaultBlockState(), blockInfo.state()),
            () -> this.getFenceBlockWithState(Blocks.COBBLESTONE_WALL.defaultBlockState(), blockInfo.state())
         );
      }
   }
}
