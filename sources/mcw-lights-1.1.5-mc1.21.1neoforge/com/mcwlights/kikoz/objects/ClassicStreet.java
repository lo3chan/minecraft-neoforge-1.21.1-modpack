package com.mcwlights.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClassicStreet extends LightBaseTall {
   private static final VoxelShape ONE = Block.box(5.0, 7.0, 5.0, 11.0, 15.0, 11.0);
   private static final VoxelShape TWO = Block.box(6.0, 0.0, 6.0, 10.0, 7.0, 10.0);
   private static final VoxelShape THREE = Block.box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0);
   private static final VoxelShape BASE = Shapes.or(ONE, new VoxelShape[]{TWO, THREE});
   private static final VoxelShape FOUR = Block.box(7.0, 0.0, 7.0, 9.0, 2.0, 9.0);
   private static final VoxelShape FIVE = Block.box(5.0, 2.0, 5.0, 11.0, 10.0, 11.0);
   private static final VoxelShape SIX = Block.box(6.0, 10.0, 6.0, 10.0, 11.0, 10.0);
   private static final VoxelShape TOP = Shapes.or(FOUR, new VoxelShape[]{FIVE, SIX});
   private static final VoxelShape MIDDLE_BOTTOM = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

   public ClassicStreet(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(PART, LightBaseTall.LightPart.BOTTOM))
            .setValue(POWERED, false)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((LightBaseTall.LightPart)state.getValue(PART)) {
         case BASE:
            return BASE;
         case TOP:
            return TOP;
         case MIDDLE:
            return MIDDLE_BOTTOM;
         case BOTTOM:
            return MIDDLE_BOTTOM;
         default:
            return BASE;
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, LIT, POWERED});
   }
}
