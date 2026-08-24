package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;

public class AMFrostWalker {
   public static void freezeAround(LivingEntity entity, Level level, BlockPos pos, int radiusLevel) {
      if (entity.onGround()) {
         BlockState frostedIce = Blocks.FROSTED_ICE.defaultBlockState();
         float radius = Math.min(16, 2 + radiusLevel);
         MutableBlockPos above = new MutableBlockPos();

         for (BlockPos candidate : BlockPos.betweenClosed(pos.offset((int)(-radius), -1, (int)(-radius)), pos.offset((int)radius, -1, (int)radius))) {
            if (candidate.closerToCenterThan(entity.position(), radius)) {
               above.set(candidate.getX(), candidate.getY() + 1, candidate.getZ());
               if (level.getBlockState(above).isAir()) {
                  BlockState state = level.getBlockState(candidate);
                  if (state.is(Blocks.WATER)
                     && (Integer)state.getValue(BlockStateProperties.LEVEL) == 0
                     && frostedIce.canSurvive(level, candidate)
                     && level.isUnobstructed(frostedIce, candidate, CollisionContext.empty())) {
                     level.setBlockAndUpdate(candidate, frostedIce);
                     level.scheduleTick(candidate.immutable(), Blocks.FROSTED_ICE, Mth.nextInt(entity.getRandom(), 60, 120));
                  }
               }
            }
         }
      }
   }
}
