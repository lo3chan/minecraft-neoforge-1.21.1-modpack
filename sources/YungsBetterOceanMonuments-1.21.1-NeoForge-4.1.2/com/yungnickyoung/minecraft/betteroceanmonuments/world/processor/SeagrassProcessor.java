package com.yungnickyoung.minecraft.betteroceanmonuments.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betteroceanmonuments.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.processor.ISafeWorldModifier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SeagrassProcessor extends StructureProcessor implements ISafeWorldModifier {
   public static final SeagrassProcessor INSTANCE = new SeagrassProcessor();
   public static final MapCodec<SeagrassProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.RED_SANDSTONE_SLAB)) {
         return new StructureBlockInfo(
            blockInfoGlobal.pos(), (BlockState)Blocks.TALL_SEAGRASS.defaultBlockState().setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.LOWER), null
         );
      } else if (blockInfoGlobal.state().is(Blocks.END_STONE_BRICK_SLAB)) {
         return new StructureBlockInfo(
            blockInfoGlobal.pos(), (BlockState)Blocks.TALL_SEAGRASS.defaultBlockState().setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER), null
         );
      } else {
         return blockInfoGlobal.state().is(Blocks.BRICK_SLAB)
            ? new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SEAGRASS.defaultBlockState(), null)
            : blockInfoGlobal;
      }
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SEAGRASS_PROCESSOR;
   }
}
