package com.yungnickyoung.minecraft.betteroceanmonuments.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betteroceanmonuments.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RandomPrismarineSlabDecorationProcessor extends StructureProcessor {
   public static final RandomPrismarineSlabDecorationProcessor INSTANCE = new RandomPrismarineSlabDecorationProcessor();
   public static final MapCodec<RandomPrismarineSlabDecorationProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.LIME_CONCRETE) {
         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         BlockState blockState;
         if (random.nextFloat() < 0.4F) {
            blockState = (BlockState)((BlockState)Blocks.PRISMARINE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP))
               .setValue(SlabBlock.WATERLOGGED, true);
         } else if (random.nextFloat() < 0.8F) {
            blockState = (BlockState)((BlockState)Blocks.PRISMARINE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM))
               .setValue(SlabBlock.WATERLOGGED, true);
         } else {
            blockState = Blocks.AIR.defaultBlockState();
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.RANDOM_PRISMARINE_SLAB_DECORATION_PROCESSOR;
   }
}
