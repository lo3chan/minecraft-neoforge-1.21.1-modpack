package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBananaPeel extends BushBlock {
   protected static final VoxelShape SHAPE_COLLISON = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

   protected MapCodec<? extends BushBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockBananaPeel() {
      super(Properties.of().dynamicShape().sound(SoundType.WET_GRASS).noCollission().requiresCorrectToolForDrops().strength(0.2F).friction(1.0F));
   }

   public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return canSupportRigidBlock(worldIn, pos);
   }

   public OffsetType getOffsetType() {
      return OffsetType.XZ;
   }

   @Deprecated
   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE_COLLISON;
   }

   public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
      return SHAPE_COLLISON;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
