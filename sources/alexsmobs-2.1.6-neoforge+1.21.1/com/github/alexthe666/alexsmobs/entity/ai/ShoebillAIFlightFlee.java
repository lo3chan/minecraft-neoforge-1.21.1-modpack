package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public class ShoebillAIFlightFlee extends Goal {
   private final EntityShoebill bird;
   private BlockPos currentTarget = null;
   private int executionTime = 0;

   public ShoebillAIFlightFlee(EntityShoebill bird) {
      this.setFlags(EnumSet.of(Flag.MOVE));
      this.bird = bird;
   }

   public void stop() {
      this.currentTarget = null;
      this.executionTime = 0;
      this.bird.setFlying(false);
   }

   public boolean canContinueToUse() {
      return this.bird.isFlying() && (this.executionTime < 15 || !this.bird.onGround());
   }

   public boolean canUse() {
      return this.bird.revengeCooldown > 0 && this.bird.onGround();
   }

   public void start() {
      if (this.bird.onGround()) {
         this.bird.setFlying(true);
      }
   }

   public void tick() {
      this.executionTime++;
      if (this.currentTarget == null) {
         if (this.bird.revengeCooldown == 0) {
            this.currentTarget = this.getBlockGrounding(this.bird.position());
         } else {
            this.currentTarget = this.getBlockInViewAway(this.bird.position());
         }
      }

      if (this.currentTarget != null) {
         this.bird.getNavigation().moveTo(this.currentTarget.getX() + 0.5F, this.currentTarget.getY() + 0.5F, this.currentTarget.getZ() + 0.5F, 1.0);
         if (this.bird.distanceToSqr(Vec3.atCenterOf(this.currentTarget)) < 4.0) {
            this.currentTarget = null;
         }
      }

      if (this.bird.revengeCooldown == 0 && (this.bird.isInWater() || !this.bird.level().isEmptyBlock(this.bird.blockPosition().below()))) {
         this.stop();
         this.bird.setFlying(false);
      }
   }

   public BlockPos getBlockInViewAway(Vec3 fleePos) {
      float radius = -9.45F - this.bird.getRandom().nextInt(24);
      float neg = this.bird.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.bird.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.bird.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, 0.0, fleePos.z() + extraZ);
      BlockPos ground = this.bird.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
      int distFromGround = (int)this.bird.getY() - ground.getY();
      int flightHeight = 4 + this.bird.getRandom().nextInt(10);
      BlockPos newPos = radialPos.above(distFromGround > 8 ? flightHeight : (int)this.bird.getY() + this.bird.getRandom().nextInt(6) + 1);
      return !this.bird.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.bird.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0 ? newPos : null;
   }

   public BlockPos getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.bird.getRandom().nextInt(24);
      float neg = this.bird.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.bird.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.bird.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, 0.0, fleePos.z() + extraZ);
      BlockPos ground = this.bird.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
      return !this.bird.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? ground : null;
   }
}
