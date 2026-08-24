package com.yungnickyoung.minecraft.yungsextras.world.processor;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface INbtFeatureProcessor {
   void processTemplate(StructureTemplate var1, WorldGenLevel var2, RandomSource var3, BlockPos var4, BlockPos var5, StructurePlaceSettings var6);

   default void generatePillarDown(WorldGenLevel level, BlockPos pos, Supplier<BlockState> replacementBlockSupplier, Supplier<BlockState> legBlockSupplier) {
      level.setBlock(pos, replacementBlockSupplier.get(), 2);
      MutableBlockPos mutable = pos.below().mutable();

      for (BlockState currBlock = level.getBlockState(mutable);
         mutable.getY() > 0 && (currBlock.isAir() || currBlock.liquid());
         currBlock = level.getBlockState(mutable)
      ) {
         level.setBlock(mutable, legBlockSupplier.get(), 2);
         mutable.move(Direction.DOWN);
      }
   }

   default BlockState getStairsBlockWithState(BlockState input, BlockState source) {
      return input.getBlock() instanceof StairBlock
         ? (BlockState)((BlockState)((BlockState)((BlockState)input.setValue(
                     StairBlock.FACING, source.hasProperty(StairBlock.FACING) ? (Direction)source.getValue(StairBlock.FACING) : Direction.NORTH
                  ))
                  .setValue(StairBlock.HALF, source.hasProperty(StairBlock.HALF) ? (Half)source.getValue(StairBlock.HALF) : Half.BOTTOM))
               .setValue(StairBlock.SHAPE, source.hasProperty(StairBlock.SHAPE) ? (StairsShape)source.getValue(StairBlock.SHAPE) : StairsShape.STRAIGHT))
            .setValue(StairBlock.WATERLOGGED, source.hasProperty(StairBlock.WATERLOGGED) ? (Boolean)source.getValue(StairBlock.WATERLOGGED) : false)
         : input;
   }

   default BlockState getSlabBlockWithState(BlockState input, BlockState source) {
      return input.getBlock() instanceof SlabBlock
         ? (BlockState)((BlockState)input.setValue(
               SlabBlock.TYPE, source.hasProperty(SlabBlock.TYPE) ? (SlabType)source.getValue(SlabBlock.TYPE) : SlabType.BOTTOM
            ))
            .setValue(SlabBlock.WATERLOGGED, source.hasProperty(SlabBlock.WATERLOGGED) ? (Boolean)source.getValue(SlabBlock.WATERLOGGED) : false)
         : input;
   }

   default BlockState getWallBlockWithState(BlockState input, BlockState source) {
      return input.getBlock() instanceof WallBlock && source.getBlock() instanceof WallBlock
         ? (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)input.setValue(
                           WallBlock.NORTH_WALL, source.hasProperty(WallBlock.NORTH_WALL) ? (WallSide)source.getValue(WallBlock.NORTH_WALL) : WallSide.NONE
                        ))
                        .setValue(WallBlock.EAST_WALL, source.hasProperty(WallBlock.EAST_WALL) ? (WallSide)source.getValue(WallBlock.EAST_WALL) : WallSide.NONE))
                     .setValue(WallBlock.SOUTH_WALL, source.hasProperty(WallBlock.SOUTH_WALL) ? (WallSide)source.getValue(WallBlock.SOUTH_WALL) : WallSide.NONE))
                  .setValue(WallBlock.WEST_WALL, source.hasProperty(WallBlock.WEST_WALL) ? (WallSide)source.getValue(WallBlock.WEST_WALL) : WallSide.NONE))
               .setValue(WallBlock.UP, source.hasProperty(WallBlock.UP) ? (Boolean)source.getValue(WallBlock.UP) : true))
            .setValue(WallBlock.WATERLOGGED, source.hasProperty(WallBlock.WATERLOGGED) ? (Boolean)source.getValue(WallBlock.WATERLOGGED) : false)
         : input;
   }

   default BlockState getFenceBlockWithState(BlockState input, BlockState source) {
      return input.getBlock() instanceof FenceBlock
         ? (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)input.setValue(
                        FenceBlock.NORTH, source.hasProperty(FenceBlock.NORTH) ? (Boolean)source.getValue(FenceBlock.NORTH) : false
                     ))
                     .setValue(FenceBlock.EAST, source.hasProperty(FenceBlock.EAST) ? (Boolean)source.getValue(FenceBlock.EAST) : false))
                  .setValue(FenceBlock.SOUTH, source.hasProperty(FenceBlock.SOUTH) ? (Boolean)source.getValue(FenceBlock.SOUTH) : false))
               .setValue(FenceBlock.WEST, source.hasProperty(FenceBlock.WEST) ? (Boolean)source.getValue(FenceBlock.WEST) : false))
            .setValue(FenceBlock.WATERLOGGED, source.hasProperty(FenceBlock.WATERLOGGED) ? (Boolean)source.getValue(FenceBlock.WATERLOGGED) : false)
         : input;
   }

   default BlockState getLogBlockWithState(BlockState input, BlockState source) {
      return input.getBlock() instanceof RotatedPillarBlock
         ? (BlockState)input.setValue(
            RotatedPillarBlock.AXIS, source.hasProperty(RotatedPillarBlock.AXIS) ? (Axis)source.getValue(RotatedPillarBlock.AXIS) : Axis.Y
         )
         : input;
   }

   default BlockState getLanternBlockWithState(BlockState input, BlockState source) {
      return input.getBlock() instanceof LanternBlock
         ? (BlockState)((BlockState)input.setValue(
               LanternBlock.HANGING, source.hasProperty(LanternBlock.HANGING) ? (Boolean)source.getValue(LanternBlock.HANGING) : true
            ))
            .setValue(LanternBlock.WATERLOGGED, source.hasProperty(LanternBlock.WATERLOGGED) ? (Boolean)source.getValue(LanternBlock.WATERLOGGED) : true)
         : input;
   }
}
