package com.yungnickyoung.minecraft.yungsbridges.world.processor;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class LanternRotProcessor implements ITemplateFeatureProcessor {
   private final BlockStateRandomizer lanternSelector = new BlockStateRandomizer(AIR).addBlock(Blocks.LANTERN.defaultBlockState(), 0.5F);
   private final BlockStateRandomizer torchSelector = new BlockStateRandomizer(AIR).addBlock(Blocks.TORCH.defaultBlockState(), 0.5F);
   private final BlockStateRandomizer wallTorchSelector = new BlockStateRandomizer(AIR).addBlock(Blocks.WALL_TORCH.defaultBlockState(), 0.5F);

   @Override
   public void processTemplate(
      StructureTemplate template,
      WorldGenLevel level,
      RandomSource randomSource,
      BlockPos cornerPos,
      BlockPos centerPos,
      StructurePlaceSettings placementSettings
   ) {
      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.LANTERN)) {
         level.setBlock(blockInfo.pos(), this.getLanternBlockWithState(this.lanternSelector.get(randomSource), blockInfo.state()), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.TORCH)) {
         level.setBlock(blockInfo.pos(), this.torchSelector.get(randomSource), 2);
      }

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.WALL_TORCH)) {
         level.setBlock(blockInfo.pos(), this.wallTorchSelector.get(randomSource), 2);
      }
   }
}
