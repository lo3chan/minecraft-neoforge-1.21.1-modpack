package com.yungnickyoung.minecraft.betterjungletemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterjungletemples.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlastFurnaceProcessor extends StructureProcessor {
   public static final BlastFurnaceProcessor INSTANCE = new BlastFurnaceProcessor();
   public static final MapCodec<BlastFurnaceProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private static final BlockStateRandomizer SELECTOR = new BlockStateRandomizer(Blocks.DISPENSER.defaultBlockState())
      .addBlock(Blocks.DROPPER.defaultBlockState(), 0.4F)
      .addBlock(Blocks.OBSERVER.defaultBlockState(), 0.2F);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.BLAST_FURNACE)) {
         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         BlockState blockState = SELECTOR.get(randomSource);
         blockState = (BlockState)blockState.setValue(DirectionalBlock.FACING, (Direction)blockInfoGlobal.state().getValue(BlastFurnaceBlock.FACING));
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, null);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.BLAST_FURNACE_PROCESSOR;
   }
}
