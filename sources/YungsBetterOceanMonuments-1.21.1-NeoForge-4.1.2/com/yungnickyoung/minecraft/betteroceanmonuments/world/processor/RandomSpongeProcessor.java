package com.yungnickyoung.minecraft.betteroceanmonuments.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betteroceanmonuments.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.processor.ISafeWorldModifier;
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
public class RandomSpongeProcessor extends StructureProcessor implements ISafeWorldModifier {
   public static final RandomSpongeProcessor INSTANCE = new RandomSpongeProcessor();
   public static final MapCodec<RandomSpongeProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.ORANGE_STAINED_GLASS)) {
         return structurePlacementData.getRandom(blockInfoGlobal.pos()).nextFloat() < 0.75F
            ? new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.WET_SPONGE.defaultBlockState(), null)
            : new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.WATER.defaultBlockState(), null);
      } else {
         return blockInfoGlobal;
      }
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SPONGE_PROCESSOR;
   }
}
