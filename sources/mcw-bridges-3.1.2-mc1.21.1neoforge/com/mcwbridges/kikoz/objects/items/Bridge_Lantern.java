package com.mcwbridges.kikoz.objects.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Bridge_Lantern extends Bridge_Torch {
   protected static final VoxelShape EAST = Block.box(5.5, 0.0, 0.0, 10.5, 7.0, 4.0);
   protected static final VoxelShape SOUTH = Block.box(12.0, 0.0, 5.5, 16.0, 7.0, 10.5);
   protected static final VoxelShape WEST = Block.box(5.5, 0.0, 12.0, 10.5, 7.0, 16.0);
   protected static final VoxelShape NORTH = Block.box(0.0, 0.0, 5.5, 4.0, 7.0, 10.5);
   protected static final VoxelShape EAST_STAIR = Block.box(5.5, 7.0, 0.0, 10.5, 16.0, 4.0);
   protected static final VoxelShape SOUTH_STAIR = Block.box(12.0, 7.0, 5.5, 16.0, 16.0, 10.5);
   protected static final VoxelShape WEST_STAIR = Block.box(5.5, 7.0, 12.0, 10.5, 16.0, 16.0);
   protected static final VoxelShape NORTH_STAIR = Block.box(0.0, 7.0, 5.5, 4.0, 16.0, 10.5);
   protected int lightValue;

   public Bridge_Lantern(Properties prop, int lightValue) {
      super(prop, 15);
      this.lightValue = lightValue;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(LIGHTSTATE, Bridge_Torch.LightState.BRIDGE)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      Direction facing = (Direction)state.getValue(FACING);
      Bridge_Torch.LightState lightState = (Bridge_Torch.LightState)state.getValue(LIGHTSTATE);
      if (lightState == Bridge_Torch.LightState.BRIDGE) {
         switch (facing) {
            case NORTH:
               return NORTH;
            case SOUTH:
               return SOUTH;
            case WEST:
               return WEST;
            case EAST:
            default:
               return EAST;
         }
      } else {
         switch (facing) {
            case NORTH:
               return NORTH_STAIR;
            case SOUTH:
               return SOUTH_STAIR;
            case WEST:
               return WEST_STAIR;
            case EAST:
            default:
               return EAST_STAIR;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, LIGHTSTATE});
   }
}
