package com.yungnickyoung.minecraft.betterendisland.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterendisland.module.StructureProcessorTypeModule;
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
public class DragonEggProcessor extends StructureProcessor {
   public static final DragonEggProcessor INSTANCE = new DragonEggProcessor();
   public static final MapCodec<DragonEggProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (levelReader.getBlockState(blockInfoGlobal.pos()).is(Blocks.DRAGON_EGG)) {
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.DRAGON_EGG.defaultBlockState(), blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.DRAGON_EGG_PROCESSOR;
   }
}
