package com.mcwfences.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceHitbox extends FenceBlock {
   protected static final VoxelShape POST = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   protected static final VoxelShape SIDE_0 = Block.box(5.0, 0.0, 0.0, 11.0, 16.0, 8.0);
   protected static final VoxelShape SIDE_90 = Block.box(8.0, 0.0, 5.0, 16.0, 16.0, 11.0);
   protected static final VoxelShape SIDE_180 = Block.box(5.0, 0.0, 8.0, 11.0, 16.0, 16.0);
   protected static final VoxelShape SIDE_270 = Block.box(0.0, 0.0, 5.0, 8.0, 16.0, 11.0);

   public FenceHitbox(Properties prop) {
      super(prop);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false))
                  .setValue(SOUTH, false))
               .setValue(WEST, false))
            .setValue(WATERLOGGED, false)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      boolean north = (Boolean)state.getValue(NORTH);
      boolean east = (Boolean)state.getValue(EAST);
      boolean south = (Boolean)state.getValue(SOUTH);
      boolean west = (Boolean)state.getValue(WEST);
      if (north && east && south && west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_0, SIDE_90, SIDE_180, SIDE_270});
      } else if (north && east && south && !west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_0, SIDE_90, SIDE_180});
      } else if (north && east && !south && west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_0, SIDE_90, SIDE_270});
      } else if (north && !east && south && west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_0, SIDE_180, SIDE_270});
      } else if (!north && east && south && west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_90, SIDE_180, SIDE_270});
      } else if (!north && east && !south && west) {
         return Shapes.or(SIDE_90, SIDE_270);
      } else if (north && !east && south && !west) {
         return Shapes.or(SIDE_0, SIDE_180);
      } else if (north && !east && !south && west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_0, SIDE_270});
      } else if (!north && east && south && !west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_90, SIDE_180});
      } else if (!north && !east && south && west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_180, SIDE_270});
      } else if (north && east && !south && !west) {
         return Shapes.or(POST, new VoxelShape[]{SIDE_0, SIDE_90});
      } else if (!north && !east && south && !west) {
         return Shapes.or(POST, SIDE_180);
      } else if (!north && !east && !south && west) {
         return Shapes.or(POST, SIDE_270);
      } else if (north && !east && !south && !west) {
         return Shapes.or(POST, SIDE_0);
      } else {
         return !north && east && !south && !west ? Shapes.or(POST, SIDE_90) : POST;
      }
   }
}
