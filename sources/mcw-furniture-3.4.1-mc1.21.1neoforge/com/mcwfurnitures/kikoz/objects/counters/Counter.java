package com.mcwfurnitures.kikoz.objects.counters;

import com.mcwfurnitures.kikoz.objects.FurnitureObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Counter extends FurnitureObject {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final VoxelShape VNORTH = Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 15.0), new VoxelShape[0]);
   protected static final VoxelShape VEAST = Shapes.or(Block.box(1.0, 0.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape VSOUTH = Shapes.or(Block.box(0.0, 0.0, 1.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape VWEST = Shapes.or(Block.box(0.0, 0.0, 0.0, 15.0, 16.0, 16.0), new VoxelShape[0]);

   public Counter(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return VNORTH;
         case EAST:
            return VEAST;
         case SOUTH:
            return VSOUTH;
         case WEST:
            return VWEST;
         default:
            return VNORTH;
      }
   }

   @Override
   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Override
   public RenderShape getRenderShape(BlockState shape) {
      return RenderShape.MODEL;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }
}
