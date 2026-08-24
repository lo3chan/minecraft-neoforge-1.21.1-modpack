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

public class OptionalSlabProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer purpurSlabReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.STONE_BRICK_SLAB.defaultBlockState(), 0.75F);
   private final BlockStateRandomizer endStoneBrickSlabReplacer = new BlockStateRandomizer(AIR)
      .addBlock(Blocks.POLISHED_ANDESITE_SLAB.defaultBlockState(), 0.75F);
   private final BlockStateRandomizer darkPrismarineSlabReplacer = new BlockStateRandomizer(AIR)
      .addBlock(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 0.45F)
      .addBlock(Blocks.ANDESITE_SLAB.defaultBlockState(), 0.45F);
   private final BlockStateRandomizer prismarineSlabReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 0.9F);
   private final BlockStateRandomizer warpedSlabReplacer = new BlockStateRandomizer(AIR).addBlock(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 0.9F);

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

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.PURPUR_SLAB)) {
         level.setBlock(blockInfo.pos(), this.getSlabBlockWithState(this.purpurSlabReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.END_STONE_BRICK_SLAB)) {
         level.setBlock(blockInfo.pos(), this.getSlabBlockWithState(this.endStoneBrickSlabReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.DARK_PRISMARINE_SLAB)) {
         level.setBlock(blockInfo.pos(), this.getSlabBlockWithState(this.darkPrismarineSlabReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.PRISMARINE_BRICK_SLAB)) {
         level.setBlock(blockInfo.pos(), this.getSlabBlockWithState(this.prismarineSlabReplacer.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.CRIMSON_SLAB)) {
         if (randomSource.nextFloat() < 0.5F) {
            level.setBlock(blockInfo.pos(), this.getSlabBlockWithState(this.getSlabBiomeVariant(biome), blockInfo.state()), 2);
         } else {
            level.setBlock(blockInfo.pos(), Blocks.AIR.defaultBlockState(), 2);
         }
      }

      for (StructureBlockInfo blockInfox : template.filterBlocks(cornerPos, placementSettings, Blocks.WARPED_SLAB)) {
         level.setBlock(blockInfox.pos(), this.getSlabBlockWithState(this.warpedSlabReplacer.get(randomSource), blockInfox.state()), 2);
      }
   }
}
