package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.util.WindowPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArrowSill extends WindowBase {
   String infoname;
   boolean hasTextInfo = true;
   protected static final VoxelShape NORTH = Shapes.or(
      Block.box(1.0, 3.0, 0.0, 5.0, 13.0, 6.0),
      new VoxelShape[]{Block.box(0.0, 13.0, 0.0, 6.0, 16.0, 16.0), Block.box(1.0, 3.0, 10.0, 5.0, 13.0, 16.0), Block.box(0.0, 0.0, 0.0, 6.0, 3.0, 16.0)}
   );
   protected static final VoxelShape EAST = Shapes.or(
      Block.box(10.0, 3.0, 11.0, 16.0, 13.0, 15.0),
      new VoxelShape[]{Block.box(0.0, 13.0, 10.0, 16.0, 16.0, 16.0), Block.box(0.0, 3.0, 11.0, 6.0, 13.0, 15.0), Block.box(0.0, 0.0, 10.0, 16.0, 3.0, 16.0)}
   );
   protected static final VoxelShape SOUTH = Shapes.or(
      Block.box(11.0, 3.0, 10.0, 15.0, 13.0, 16.0),
      new VoxelShape[]{Block.box(10.0, 13.0, 0.0, 16.0, 16.0, 16.0), Block.box(11.0, 3.0, 0.0, 15.0, 13.0, 6.0), Block.box(10.0, 0.0, 0.0, 16.0, 3.0, 16.0)}
   );
   protected static final VoxelShape WEST = Shapes.or(
      Block.box(0.0, 3.0, 1.0, 6.0, 13.0, 5.0),
      new VoxelShape[]{Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 6.0), Block.box(10.0, 3.0, 1.0, 16.0, 13.0, 5.0), Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 6.0)}
   );
   protected static final VoxelShape NORTH_TOP = Shapes.or(
      Block.box(1.0, 1.0, 10.0, 5.0, 10.0, 16.0),
      new VoxelShape[]{
         Block.box(1.0, 1.0, 0.0, 5.0, 10.0, 6.0),
         Block.box(1.0, 0.0, 0.0, 5.0, 1.0, 3.0),
         Block.box(1.0, 0.0, 13.0, 5.0, 1.0, 16.0),
         Block.box(0.0, 10.0, 0.0, 6.0, 16.0, 16.0)
      }
   );
   protected static final VoxelShape EAST_TOP = Shapes.or(
      Block.box(0.0, 1.0, 11.0, 6.0, 10.0, 15.0),
      new VoxelShape[]{
         Block.box(10.0, 1.0, 11.0, 16.0, 10.0, 15.0),
         Block.box(13.0, 0.0, 11.0, 16.0, 1.0, 15.0),
         Block.box(0.0, 0.0, 11.0, 3.0, 1.0, 15.0),
         Block.box(0.0, 10.0, 10.0, 16.0, 16.0, 16.0)
      }
   );
   protected static final VoxelShape SOUTH_TOP = Shapes.or(
      Block.box(11.0, 1.0, 0.0, 15.0, 10.0, 6.0),
      new VoxelShape[]{
         Block.box(11.0, 1.0, 10.0, 15.0, 10.0, 16.0),
         Block.box(11.0, 0.0, 13.0, 15.0, 1.0, 16.0),
         Block.box(11.0, 0.0, 0.0, 15.0, 1.0, 3.0),
         Block.box(10.0, 10.0, 0.0, 16.0, 16.0, 16.0)
      }
   );
   protected static final VoxelShape WEST_TOP = Shapes.or(
      Block.box(10.0, 1.0, 1.0, 16.0, 10.0, 5.0),
      new VoxelShape[]{
         Block.box(0.0, 1.0, 1.0, 6.0, 10.0, 5.0),
         Block.box(0.0, 0.0, 1.0, 3.0, 1.0, 5.0),
         Block.box(13.0, 0.0, 1.0, 16.0, 1.0, 5.0),
         Block.box(0.0, 10.0, 0.0, 16.0, 16.0, 6.0)
      }
   );
   protected static final VoxelShape NORTH_MIDDLE = Shapes.or(
      Block.box(1.0, 1.0, 10.0, 5.0, 15.0, 16.0),
      new VoxelShape[]{
         Block.box(1.0, 1.0, 0.0, 5.0, 15.0, 6.0),
         Block.box(1.0, 0.0, 0.0, 5.0, 1.0, 3.0),
         Block.box(1.0, 0.0, 13.0, 5.0, 1.0, 16.0),
         Block.box(1.0, 15.0, 0.0, 5.0, 16.0, 3.0),
         Block.box(1.0, 15.0, 13.0, 5.0, 16.0, 16.0)
      }
   );
   protected static final VoxelShape EAST_MIDDLE = Shapes.or(
      Block.box(0.0, 1.0, 11.0, 6.0, 15.0, 15.0),
      new VoxelShape[]{
         Block.box(10.0, 1.0, 11.0, 16.0, 15.0, 15.0),
         Block.box(13.0, 0.0, 11.0, 16.0, 1.0, 15.0),
         Block.box(0.0, 0.0, 11.0, 3.0, 1.0, 15.0),
         Block.box(13.0, 15.0, 11.0, 16.0, 16.0, 15.0),
         Block.box(0.0, 15.0, 11.0, 3.0, 16.0, 15.0)
      }
   );
   protected static final VoxelShape SOUTH_MIDDLE = Shapes.or(
      Block.box(11.0, 1.0, 0.0, 15.0, 15.0, 6.0),
      new VoxelShape[]{
         Block.box(11.0, 1.0, 10.0, 15.0, 15.0, 16.0),
         Block.box(11.0, 0.0, 13.0, 15.0, 1.0, 16.0),
         Block.box(11.0, 0.0, 0.0, 15.0, 1.0, 3.0),
         Block.box(11.0, 15.0, 13.0, 15.0, 16.0, 16.0),
         Block.box(11.0, 15.0, 0.0, 15.0, 16.0, 3.0)
      }
   );
   protected static final VoxelShape WEST_MIDDLE = Shapes.or(
      Block.box(10.0, 1.0, 1.0, 16.0, 15.0, 5.0),
      new VoxelShape[]{
         Block.box(0.0, 1.0, 1.0, 6.0, 15.0, 5.0),
         Block.box(0.0, 0.0, 1.0, 3.0, 1.0, 5.0),
         Block.box(13.0, 0.0, 1.0, 16.0, 1.0, 5.0),
         Block.box(0.0, 15.0, 1.0, 3.0, 16.0, 5.0),
         Block.box(13.0, 15.0, 1.0, 16.0, 16.0, 5.0)
      }
   );
   protected static final VoxelShape NORTH_BOT = Shapes.or(
      Block.box(1.0, 6.0, 0.0, 5.0, 15.0, 6.0),
      new VoxelShape[]{
         Block.box(1.0, 6.0, 10.0, 5.0, 15.0, 16.0),
         Block.box(1.0, 15.0, 13.0, 5.0, 16.0, 16.0),
         Block.box(1.0, 15.0, 0.0, 5.0, 16.0, 3.0),
         Block.box(0.0, 0.0, 0.0, 6.0, 6.0, 16.0)
      }
   );
   protected static final VoxelShape EAST_BOT = Shapes.or(
      Block.box(10.0, 6.0, 11.0, 16.0, 15.0, 15.0),
      new VoxelShape[]{
         Block.box(0.0, 6.0, 11.0, 6.0, 15.0, 15.0),
         Block.box(0.0, 15.0, 11.0, 3.0, 16.0, 15.0),
         Block.box(13.0, 15.0, 11.0, 16.0, 16.0, 15.0),
         Block.box(0.0, 0.0, 10.0, 16.0, 6.0, 16.0)
      }
   );
   protected static final VoxelShape SOUTH_BOT = Shapes.or(
      Block.box(11.0, 6.0, 10.0, 15.0, 15.0, 16.0),
      new VoxelShape[]{
         Block.box(11.0, 6.0, 0.0, 15.0, 15.0, 6.0),
         Block.box(11.0, 15.0, 0.0, 15.0, 16.0, 3.0),
         Block.box(11.0, 15.0, 13.0, 15.0, 16.0, 16.0),
         Block.box(10.0, 0.0, 0.0, 16.0, 6.0, 16.0)
      }
   );
   protected static final VoxelShape WEST_BOT = Shapes.or(
      Block.box(0.0, 6.0, 1.0, 6.0, 15.0, 5.0),
      new VoxelShape[]{
         Block.box(10.0, 6.0, 1.0, 16.0, 15.0, 5.0),
         Block.box(13.0, 15.0, 1.0, 16.0, 16.0, 5.0),
         Block.box(0.0, 15.0, 1.0, 3.0, 16.0, 5.0),
         Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 6.0)
      }
   );

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case EAST:
            switch ((WindowPart)state.getValue(PART)) {
               case BASE:
                  return WEST;
               case TOP:
                  return WEST_TOP;
               case MIDDLE:
                  return WEST_MIDDLE;
               case BOTTOM:
                  return WEST_BOT;
            }
         case WEST:
            switch ((WindowPart)state.getValue(PART)) {
               case BASE:
                  return EAST;
               case TOP:
                  return EAST_TOP;
               case MIDDLE:
                  return EAST_MIDDLE;
               case BOTTOM:
                  return EAST_BOT;
            }
         case SOUTH:
            switch ((WindowPart)state.getValue(PART)) {
               case BASE:
                  return SOUTH;
               case TOP:
                  return SOUTH_TOP;
               case MIDDLE:
                  return SOUTH_MIDDLE;
               case BOTTOM:
                  return SOUTH_BOT;
            }
         case NORTH:
         default:
            switch ((WindowPart)state.getValue(PART)) {
               case BASE:
                  return NORTH;
               case TOP:
                  return NORTH_TOP;
               case MIDDLE:
                  return NORTH_MIDDLE;
               case BOTTOM:
                  return NORTH_BOT;
               default:
                  return null;
            }
      }
   }

   public ArrowSill(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(PART, WindowPart.BASE)
      );
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, FACING});
   }
}
