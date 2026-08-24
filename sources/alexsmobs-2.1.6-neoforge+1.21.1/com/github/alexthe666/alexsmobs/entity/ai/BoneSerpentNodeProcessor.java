package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;

public class BoneSerpentNodeProcessor extends NodeEvaluator {
   public Node getStart() {
      return super.getNode(
         Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ)
      );
   }

   public Target getTarget(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new Target(
         super.getNode(
            Mth.floor(p_224768_1_ - this.mob.getBbWidth() / 2.0F), Mth.floor(p_224768_3_ + 0.5), Mth.floor(p_224768_5_ - this.mob.getBbWidth() / 2.0F)
         )
      );
   }

   public int getNeighbors(Node[] p_222859_1_, Node p_222859_2_) {
      int i = 0;

      for (Direction direction : Direction.values()) {
         Node pathpoint = this.getWaterNode(p_222859_2_.x + direction.getStepX(), p_222859_2_.y + direction.getStepY(), p_222859_2_.z + direction.getStepZ());
         if (pathpoint != null && !pathpoint.closed) {
            p_222859_1_[i++] = pathpoint;
         }
      }

      return i;
   }

   public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
      return this.getBlockPathType(context.level(), x, y, z);
   }

   public PathType getPathType(PathfindingContext context, int x, int y, int z) {
      return this.getBlockPathType(context.level(), x, y, z);
   }

   public PathType getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      FluidState fluidstate = blockaccessIn.getFluidState(blockpos);
      BlockState blockstate = blockaccessIn.getBlockState(blockpos);
      if (fluidstate.isEmpty() && AMCompat.isPathfindable(blockstate, blockaccessIn, blockpos.below(), PathComputationType.WATER) && blockstate.isAir()) {
         return PathType.BREACH;
      } else {
         return !fluidstate.is(FluidTags.LAVA)
               && (!fluidstate.is(FluidTags.WATER) || !AMCompat.isPathfindable(blockstate, blockaccessIn, blockpos, PathComputationType.WATER))
            ? PathType.BLOCKED
            : PathType.WATER;
      }
   }

   private BlockGetter blocks() {
      return this.currentContext.level();
   }

   @Nullable
   private Node getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
      PathType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
      return pathnodetype != PathType.BREACH && pathnodetype != PathType.WATER && pathnodetype != PathType.LAVA
         ? null
         : this.getNode(p_186328_1_, p_186328_2_, p_186328_3_);
   }

   @Nullable
   protected Node getNode(int x, int y, int z) {
      Node pathpoint = null;
      PathType pathnodetype = this.getBlockPathType(this.mob.level(), x, y, z);
      float f = this.mob.getPathfindingMalus(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.getNode(x, y, z);
         pathpoint.type = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (this.blocks().getFluidState(new BlockPos(x, y, z)).isEmpty()) {
            pathpoint.costMalus += 8.0F;
         }
      }

      return pathnodetype == PathType.OPEN ? pathpoint : pathpoint;
   }

   private PathType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
      MutableBlockPos blockpos$mutable = new MutableBlockPos();
      BlockGetter blocks = this.blocks();

      for (int i = p_186327_1_; i < p_186327_1_ + this.entityWidth; i++) {
         for (int j = p_186327_2_; j < p_186327_2_ + this.entityHeight; j++) {
            for (int k = p_186327_3_; k < p_186327_3_ + this.entityDepth; k++) {
               FluidState fluidstate = blocks.getFluidState(blockpos$mutable.set(i, j, k));
               BlockState blockstate = blocks.getBlockState(blockpos$mutable.set(i, j, k));
               if (fluidstate.isEmpty()
                  && AMCompat.isPathfindable(blockstate, blocks, blockpos$mutable.below(), PathComputationType.WATER)
                  && blockstate.isAir()) {
                  return PathType.BREACH;
               }

               if (!fluidstate.is(FluidTags.WATER) && !fluidstate.is(FluidTags.LAVA)) {
                  return PathType.BLOCKED;
               }
            }
         }
      }

      BlockState blockstate1 = blocks.getBlockState(blockpos$mutable);
      return !blockstate1.getFluidState().is(FluidTags.LAVA) && !AMCompat.isPathfindable(blockstate1, blocks, blockpos$mutable, PathComputationType.WATER)
         ? PathType.BLOCKED
         : PathType.WATER;
   }
}
