package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AnimalAILeapRandomly extends Goal {
   private final PathfinderMob mob;
   private final int chance;
   private final int maxLeapDistance;
   private Vec3 leapToPos = null;

   public AnimalAILeapRandomly(PathfinderMob mob, int chance, int maxLeapDistance) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.mob = mob;
      this.chance = chance;
      this.maxLeapDistance = maxLeapDistance;
   }

   public boolean canUse() {
      if (this.mob.getRandom().nextInt(this.chance) == 0 && this.mob.onGround() && this.mob.getNavigation().isDone()) {
         Vec3 found = LandRandomPos.getPos(this.mob, this.maxLeapDistance, this.maxLeapDistance);
         if (found != null && this.mob.distanceToSqr(found) < this.maxLeapDistance * this.maxLeapDistance && this.hasLineOfSightBlock(found)) {
            this.leapToPos = found;
            return true;
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.leapToPos != null
         && this.mob.distanceToSqr(this.leapToPos) < this.maxLeapDistance * this.maxLeapDistance
         && this.hasLineOfSightBlock(this.leapToPos);
   }

   private boolean hasLineOfSightBlock(Vec3 blockVec) {
      Vec3 Vector3d = new Vec3(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
      BlockHitResult result = this.mob.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this.mob));
      return blockVec.distanceTo(result.getLocation()) < 1.2000000476837158;
   }

   public void stop() {
      super.stop();
      this.leapToPos = null;
   }

   public void start() {
      if (this.leapToPos != null) {
         Vec3 vector3d = this.mob.getDeltaMovement();
         Vec3 vector3d1 = new Vec3(this.leapToPos.x - this.mob.getX(), 0.0, this.leapToPos.z - this.mob.getZ());
         if (vector3d1.lengthSqr() > 1.0E-7) {
            vector3d1 = vector3d1.normalize().scale(0.9).add(vector3d.scale(0.8));
         }

         if (this.mob instanceof EntityBunfungus) {
            ((EntityBunfungus)this.mob).onJump();
         }

         this.mob.setDeltaMovement(vector3d1.x, 0.6000000238418579, vector3d1.z);
         this.mob.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
         this.mob.yBodyRot = this.mob.getYRot();
         this.mob.yHeadRot = this.mob.getYRot();
         this.leapToPos = null;
      }
   }
}
