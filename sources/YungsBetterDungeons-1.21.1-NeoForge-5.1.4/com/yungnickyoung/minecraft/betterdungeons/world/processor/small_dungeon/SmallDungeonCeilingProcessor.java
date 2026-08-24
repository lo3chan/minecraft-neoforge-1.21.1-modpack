package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallDungeonCeilingProcessor extends StructureProcessor {
   public static final SmallDungeonCeilingProcessor INSTANCE = new SmallDungeonCeilingProcessor();
   public static final MapCodec<SmallDungeonCeilingProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.ORANGE_STAINED_GLASS) {
         if (!levelReader.getFluidState(blockInfoGlobal.pos()).is(FluidTags.WATER) && !levelReader.getFluidState(blockInfoGlobal.pos()).is(FluidTags.LAVA)) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), levelReader.getBlockState(blockInfoGlobal.pos()), null);
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.COBBLESTONE.defaultBlockState(), null);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_DUNGEON_CEILING_PROCESSOR;
   }
}
