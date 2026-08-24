package com.mcwlights.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallWallLamp extends WallLamp {
   private static final VoxelShape NORTH = Block.box(2.0, 0.0, 2.0, 14.0, 9.0, 16.0);
   private static final VoxelShape EAST = Block.box(0.0, 0.0, 2.0, 14.0, 9.0, 14.0);
   private static final VoxelShape SOUTH = Block.box(2.0, 0.0, 0.0, 14.0, 9.0, 14.0);
   private static final VoxelShape WEST = Block.box(2.0, 0.0, 2.0, 16.0, 9.0, 14.0);

   public SmallWallLamp(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true))
               .setValue(DYE_COLOR, DyeColor.WHITE))
            .setValue(POWERED, false)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      Direction direction = (Direction)state.getValue(FACING);
      switch (direction) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            return Shapes.empty();
      }
   }
}
