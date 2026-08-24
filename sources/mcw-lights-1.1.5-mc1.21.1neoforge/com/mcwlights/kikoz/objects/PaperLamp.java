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

public class PaperLamp extends LightBaseShort {
   private static final VoxelShape ONE = Block.box(2.0, 1.0, 2.0, 14.0, 15.0, 14.0);
   private static final VoxelShape TWO = Block.box(3.0, 15.0, 3.0, 13.0, 16.0, 13.0);
   private static final VoxelShape THREE = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
   private static final VoxelShape PART_BASE_Z = Shapes.or(ONE, new VoxelShape[]{TWO, THREE});

   public PaperLamp(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(POWERED, false));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return PART_BASE_Z;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
      state.add(new Property[]{LIT, POWERED});
   }
}
