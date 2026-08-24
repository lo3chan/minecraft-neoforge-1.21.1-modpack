package com.aetherteam.aether.block.miscellaneous;

import com.aetherteam.aether.block.MeltingBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;

public class UnstableObsidianBlock extends Block implements MeltingBehavior {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

   public UnstableObsidianBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(AGE, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.tick(state, level, pos, random);
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      MeltingBehavior.super.tick(this, state, level, pos, random, AGE);
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      if (block.defaultBlockState().is(this) && MeltingBehavior.super.fewerNeigboursThan(block, level, pos, 2)) {
         this.melt(state, level, pos, AGE);
      }

      super.neighborChanged(state, level, pos, block, fromPos, isMoving);
   }

   @Override
   public void melt(BlockState state, Level level, BlockPos pos, IntegerProperty age) {
      level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
      level.neighborChanged(pos, Blocks.LAVA, pos);
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.NORMAL;
   }
}
