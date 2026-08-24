package com.yungnickyoung.minecraft.betteroceanmonuments.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betteroceanmonuments.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StructureVoidProcessor extends StructureProcessor {
   public static final StructureVoidProcessor INSTANCE = new StructureVoidProcessor();
   public static final MapCodec<StructureVoidProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.PURPUR_SLAB) || blockInfoGlobal.state().is(Blocks.BROWN_WOOL)) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), levelReader.getBlockState(blockInfoGlobal.pos()), blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.STRUCTURE_VOID_PROCESSOR;
   }
}
