package com.mcwpaths.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.01, 0.0, 16.0, 0.99, 16.0);

   public PathBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
