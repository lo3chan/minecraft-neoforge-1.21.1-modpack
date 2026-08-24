package com.iafenvoy.origins.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TemporaryCobwebBlock extends WebBlock {
   public TemporaryCobwebBlock() {
      super(Properties.of().mapColor(MapColor.WOOL).strength(4.0F).requiresCorrectToolForDrops().noCollission());
   }

   protected void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
      if (!level.isClientSide) {
         level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
      }
   }

   @NotNull
   protected VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return Shapes.empty();
   }

   @NotNull
   protected VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return Shapes.empty();
   }

   protected void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
      level.scheduleTick(pos, this, 60);
      super.onPlace(state, level, pos, oldState, movedByPiston);
   }
}
