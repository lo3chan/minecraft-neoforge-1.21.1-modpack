package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EndStoneBrickWallProcessor extends StructureProcessor {
   public static final EndStoneBrickWallProcessor INSTANCE = new EndStoneBrickWallProcessor();
   public static final MapCodec<EndStoneBrickWallProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.END_STONE_BRICK_WALL) {
         BlockState blockState = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)Blocks.SANDSTONE_WALL
                           .defaultBlockState()
                           .setValue(WallBlock.EAST_WALL, (WallSide)blockInfoGlobal.state().getValue(WallBlock.EAST_WALL)))
                        .setValue(WallBlock.WEST_WALL, (WallSide)blockInfoGlobal.state().getValue(WallBlock.WEST_WALL)))
                     .setValue(WallBlock.NORTH_WALL, (WallSide)blockInfoGlobal.state().getValue(WallBlock.NORTH_WALL)))
                  .setValue(WallBlock.SOUTH_WALL, (WallSide)blockInfoGlobal.state().getValue(WallBlock.SOUTH_WALL)))
               .setValue(WallBlock.UP, (Boolean)blockInfoGlobal.state().getValue(WallBlock.UP)))
            .setValue(WallBlock.WATERLOGGED, (Boolean)blockInfoGlobal.state().getValue(WallBlock.WATERLOGGED));
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState, blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorModule.END_STONE_BRICK_WALL_PROCESSOR;
   }
}
