package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class LavaAndWaterAIRandomSwimming extends RandomStrollGoal {
   public LavaAndWaterAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
      super(creature, speed, chance, false);
   }

   public boolean canUse() {
      if (!this.mob.isVehicle() && this.mob.getTarget() == null) {
         if (!this.forceTrigger) {
            int i = !this.mob.isInLava() && !this.mob.isInWater() ? this.interval * 2 : this.interval;
            if (this.mob.getRandom().nextInt(i) != 0) {
               return false;
            }
         }

         Vec3 vector3d = this.getPosition();
         if (vector3d == null) {
            return false;
         } else {
            this.wantedX = vector3d.x;
            this.wantedY = vector3d.y;
            this.wantedZ = vector3d.z;
            this.forceTrigger = false;
            return true;
         }
      } else {
         return false;
      }
   }

   @Nullable
   protected Vec3 getPosition() {
      if (this.mob.getRandom().nextFloat() < (this.mob.isInLava() ? 0.7F : 0.3F)) {
         Vec3 vector3d = this.findSurfaceTarget(this.mob, 32, 16);
         if (vector3d != null) {
            return vector3d;
         }
      }

      Vec3 vector3d = DefaultRandomPos.getPos(this.mob, 32, 16);
      int i = 0;

      while (
         vector3d != null
            && !AMCompat.isPathfindable(
               this.mob.level().getBlockState(AMBlockPos.fromVec3(vector3d)), this.mob.level(), AMBlockPos.fromVec3(vector3d), PathComputationType.WATER
            )
            && i++ < 10
      ) {
         vector3d = DefaultRandomPos.getPos(this.mob, 10, 7);
      }

      return vector3d;
   }

   private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
      BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
      return this.mob.level().getFluidState(blockpos).is(FluidTags.WATER) && !this.mob.level().getBlockState(blockpos).blocksMotion()
         || this.mob.level().getFluidState(blockpos).is(FluidTags.LAVA);
   }

   private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
      return this.mob.level().getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir()
         && this.mob.level().getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
   }

   protected Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
      Vec3 creaturePos = creature.position();
      BlockPos upPos = creature.blockPosition();

      while (creature.level().getFluidState(upPos).is(FluidTags.LAVA) || creature.level().getFluidState(upPos).is(FluidTags.WATER)) {
         upPos = upPos.above();
      }

      return this.isAirAbove(upPos.below(), 0, 0, 0) && this.canJumpTo(upPos.below(), 0, 0, 0)
         ? new Vec3(upPos.getX() + 0.5F, upPos.getY() - 0.5F, upPos.getZ() + 0.5F)
         : null;
   }
}
