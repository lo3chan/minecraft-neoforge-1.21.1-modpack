package com.yungnickyoung.minecraft.betterstrongholds.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterstrongholds.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.betterstrongholds.world.OreChances;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class OreProcessor extends StructureProcessor {
   public static final OreProcessor INSTANCE = new OreProcessor();
   public static final MapCodec<OreProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.NETHER_GOLD_ORE) {
         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         BlockState oreBlock = OreChances.get().getRandomOre(randomSource);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), oreBlock, null);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.ORE_PROCESSOR;
   }
}
