package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallDungeonCeilingPropProcessor extends StructureProcessor {
   public static final SmallDungeonCeilingPropProcessor INSTANCE = new SmallDungeonCeilingPropProcessor();
   public static final MapCodec<SmallDungeonCeilingPropProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.MAGENTA_STAINED_GLASS)) {
         if (!levelReader.getBlockState(blockInfoGlobal.pos().above()).isFaceSturdy(levelReader, blockInfoGlobal.pos().above(), Direction.DOWN)) {
            return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         float f = random.nextFloat();
         if (f < 0.2F) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CHAIN.defaultBlockState(), blockInfoGlobal.nbt());
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), blockInfoGlobal.nbt());
         }
      } else if (blockInfoGlobal.state().is(Blocks.BROWN_STAINED_GLASS)) {
         if (!levelReader.getBlockState(blockInfoGlobal.pos().above(2)).isFaceSturdy(levelReader, blockInfoGlobal.pos().above(), Direction.DOWN)) {
            return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         float f = random.nextFloat();
         if (f < 0.5F) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CHAIN.defaultBlockState(), blockInfoGlobal.nbt());
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), blockInfoGlobal.nbt());
         }
      } else if (blockInfoGlobal.state().is(Blocks.CHAIN)
         && !levelReader.getBlockState(blockInfoGlobal.pos().above()).isFaceSturdy(levelReader, blockInfoGlobal.pos().above(), Direction.DOWN)) {
         return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CAVE_AIR.defaultBlockState(), null);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_DUNGEON_CEILING_PROP_PROCESSOR;
   }
}
