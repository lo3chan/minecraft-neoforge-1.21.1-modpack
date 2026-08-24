package com.aetherteam.aether.block.construction;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.mixin.mixins.common.accessor.BushBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.util.TriState;

public class AetherFarmBlock extends FarmBlock {
   public AetherFarmBlock(Properties properties) {
      super(properties);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
         ? ((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState()
         : this.defaultBlockState();
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         turnToDirt(state, level, pos);
      }
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      int i = (Integer)state.getValue(MOISTURE);
      if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
         if (i > 0) {
            level.setBlock(pos, (BlockState)state.setValue(MOISTURE, i - 1), 2);
         } else if (!shouldMaintainFarmland(level, pos)) {
            turnToDirt(state, level, pos);
         }
      } else if (i < 7) {
         level.setBlock(pos, (BlockState)state.setValue(MOISTURE, 7), 2);
      }
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
      if (!level.isClientSide() && CommonHooks.onFarmlandTrample(level, pos, ((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState(), fallDistance, entity)
         )
       {
         turnToDirt(state, level, pos);
      }

      entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
   }

   public static void turnToDirt(BlockState state, Level level, BlockPos pos) {
      level.setBlockAndUpdate(pos, pushEntitiesUp(state, ((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState(), level, pos));
   }

   private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
      return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
   }

   private static boolean isNearWater(LevelReader level, BlockPos pos) {
      for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
         if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
            return true;
         }
      }

      return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
   }

   public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
      return plant.getBlock() instanceof BushBlock bushBlock
            && ((BushBlockAccessor)bushBlock).callMayPlaceOn(Blocks.FARMLAND.defaultBlockState(), level, soilPosition)
         ? TriState.TRUE
         : super.canSustainPlant(state, level, soilPosition, facing, plant);
   }

   public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
      return (Integer)state.getValue(FarmBlock.MOISTURE) > 0;
   }
}
