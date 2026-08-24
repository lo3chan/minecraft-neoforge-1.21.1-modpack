package com.mcwdoors.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StableDoor extends JapaneseDoors {
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   protected static final VoxelShape EAST = Block.box(0.02, -0.02, 0.0, 3.0, 15.98, 16.0);
   protected static final VoxelShape NORTH = Block.box(0.0, -0.02, 12.98, 16.0, 15.98, 15.98);
   protected static final VoxelShape WEST = Block.box(0.0, -0.02, -0.02, 16.0, 15.98, 2.98);
   protected static final VoxelShape SOUTH = Block.box(13.02, -0.02, 0.0, 16.02, 15.98, 16.0);

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return WEST;
         case EAST:
            return EAST;
         case WEST:
         default:
            return SOUTH;
      }
   }

   @Override
   public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      boolean open = (Boolean)state.getValue(OPEN);

      return open ? Shapes.empty() : switch ((Direction)state.getValue(FACING)) {
         case NORTH -> NORTH;
         case SOUTH -> WEST;
         case EAST -> EAST;
         default -> SOUTH;
      };
   }

   public StableDoor(Properties properties, BlockSetType type) {
      super(properties, type);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                     .setValue(OPEN, false))
                  .setValue(HINGE, DoorHingeSide.LEFT))
               .setValue(POWERED, false))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }
}
