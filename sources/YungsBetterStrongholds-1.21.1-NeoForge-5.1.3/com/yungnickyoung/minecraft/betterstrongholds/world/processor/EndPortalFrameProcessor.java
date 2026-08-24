package com.yungnickyoung.minecraft.betterstrongholds.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterstrongholds.BetterStrongholdsCommon;
import com.yungnickyoung.minecraft.betterstrongholds.module.StructureProcessorTypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class EndPortalFrameProcessor extends StructureProcessor {
   public static final EndPortalFrameProcessor INSTANCE = new EndPortalFrameProcessor();
   public static final MapCodec<EndPortalFrameProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.END_PORTAL_FRAME)) {
         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         if (randomSource.nextFloat() < BetterStrongholdsCommon.CONFIG.general.filledPortalFrameChance) {
            blockInfoGlobal = new StructureBlockInfo(
               blockInfoGlobal.pos(), (BlockState)blockInfoGlobal.state().setValue(EndPortalFrameBlock.HAS_EYE, true), blockInfoGlobal.nbt()
            );
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.END_PORTAL_FRAME_PROCESSOR;
   }
}
