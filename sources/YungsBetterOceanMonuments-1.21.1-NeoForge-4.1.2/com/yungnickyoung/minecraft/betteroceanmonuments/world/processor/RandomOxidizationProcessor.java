package com.yungnickyoung.minecraft.betteroceanmonuments.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betteroceanmonuments.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RandomOxidizationProcessor extends StructureProcessor {
   public static final RandomOxidizationProcessor INSTANCE = new RandomOxidizationProcessor();
   public static final MapCodec<RandomOxidizationProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
      if (blockInfoGlobal.state().getBlock() == Blocks.OXIDIZED_COPPER) {
         BlockState blockState;
         if (random.nextFloat() < 0.1F) {
            blockState = Blocks.EXPOSED_COPPER.defaultBlockState();
         } else if (random.nextFloat() < 0.3F) {
            blockState = Blocks.WEATHERED_COPPER.defaultBlockState();
         } else {
            blockState = Blocks.OXIDIZED_COPPER.defaultBlockState();
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, blockInfoGlobal.nbt());
      } else if (blockInfoGlobal.state().getBlock() == Blocks.OXIDIZED_CUT_COPPER) {
         BlockState blockState;
         if (random.nextFloat() < 0.1F) {
            blockState = Blocks.EXPOSED_CUT_COPPER.defaultBlockState();
         } else if (random.nextFloat() < 0.3F) {
            blockState = Blocks.WEATHERED_CUT_COPPER.defaultBlockState();
         } else {
            blockState = Blocks.OXIDIZED_CUT_COPPER.defaultBlockState();
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, blockInfoGlobal.nbt());
      } else if (blockInfoGlobal.state().getBlock() == Blocks.OXIDIZED_CUT_COPPER_STAIRS) {
         BlockState blockState;
         if (random.nextFloat() < 0.1F) {
            blockState = Blocks.EXPOSED_CUT_COPPER_STAIRS.withPropertiesOf(blockInfoGlobal.state());
         } else if (random.nextFloat() < 0.3F) {
            blockState = Blocks.WEATHERED_CUT_COPPER_STAIRS.withPropertiesOf(blockInfoGlobal.state());
         } else {
            blockState = Blocks.OXIDIZED_CUT_COPPER_STAIRS.withPropertiesOf(blockInfoGlobal.state());
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, blockInfoGlobal.nbt());
      } else if (blockInfoGlobal.state().getBlock() == Blocks.OXIDIZED_CUT_COPPER_SLAB) {
         BlockState blockState;
         if (random.nextFloat() < 0.1F) {
            blockState = Blocks.EXPOSED_CUT_COPPER_SLAB.withPropertiesOf(blockInfoGlobal.state());
         } else if (random.nextFloat() < 0.3F) {
            blockState = Blocks.WEATHERED_CUT_COPPER_SLAB.withPropertiesOf(blockInfoGlobal.state());
         } else {
            blockState = Blocks.OXIDIZED_CUT_COPPER_SLAB.withPropertiesOf(blockInfoGlobal.state());
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.RANDOM_OXIDIZATION_PROCESSOR;
   }
}
