package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class KangarooAIMelee extends MeleeAttackGoal {
   private final EntityKangaroo kangaroo;
   private BlockPos waterPos;
   private int waterCheckTick = 0;
   private int waterTimeout = 0;

   public KangarooAIMelee(EntityKangaroo kangaroo, double speedIn, boolean useLongMemory) {
      super(kangaroo, speedIn, useLongMemory);
      this.kangaroo = kangaroo;
   }

   public boolean canUse() {
      return super.canUse();
   }

   public void tick() {
      boolean dontSuper = false;
      LivingEntity target = this.kangaroo.getTarget();
      if (target != null) {
         if (target == this.kangaroo.getLastHurtByMob()) {
            if (target.distanceTo(this.kangaroo) < this.kangaroo.getBbWidth() + 1.0F && target.isInWater()) {
               target.setDeltaMovement(target.getDeltaMovement().add(0.0, -0.09, 0.0));
               target.setAirSupply(target.getAirSupply() - 30);
            }

            if (this.waterPos != null && this.kangaroo.level().getFluidState(this.waterPos).is(FluidTags.WATER)) {
               this.kangaroo.setPathfindingMalus(PathType.WATER, 0.0F);
               this.kangaroo.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
               double localSpeed = Mth.clamp(this.kangaroo.distanceToSqr(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ()) * 0.5, 1.0, 2.3);
               this.kangaroo.getMoveControl().setWantedPosition(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ(), localSpeed);
               if (this.kangaroo.isInWater()) {
                  this.waterTimeout++;
               }

               if (this.waterTimeout < 1400) {
                  dontSuper = true;
                  this.amCheckAndPerformAttack(target, this.kangaroo.distanceToSqr(target));
               }

               if (this.kangaroo.isInWater() || this.kangaroo.distanceToSqr(Vec3.atCenterOf(this.waterPos)) < 10.0) {
                  this.kangaroo.totalMovingProgress = 0.0F;
               }

               if (this.kangaroo.distanceToSqr(Vec3.atCenterOf(this.waterPos)) > 10.0) {
                  this.kangaroo.setVisualFlag(0);
               }

               if (this.kangaroo.distanceToSqr(Vec3.atCenterOf(this.waterPos)) < 3.0 && this.kangaroo.isInWater()) {
                  this.kangaroo.setStanding(true);
                  this.kangaroo.maxStandTime = 100;
                  this.kangaroo.getLookControl().setLookAt(target, 360.0F, 180.0F);
                  this.kangaroo.setVisualFlag(1);
               }
            } else {
               this.kangaroo.setVisualFlag(0);
               this.waterCheckTick++;
               this.waterPos = this.generateWaterPos();
            }
         }

         if (!dontSuper) {
            super.tick();
         }
      }
   }

   public boolean canContinueToUse() {
      return this.waterPos != null && this.kangaroo.getTarget() != null || super.canContinueToUse();
   }

   public void stop() {
      super.stop();
      this.waterCheckTick = 0;
      this.waterTimeout = 0;
      this.waterPos = null;
      this.kangaroo.setVisualFlag(0);
      this.kangaroo.setPathfindingMalus(PathType.WATER, 8.0F);
      this.kangaroo.setPathfindingMalus(PathType.WATER_BORDER, 8.0F);
   }

   public BlockPos generateWaterPos() {
      BlockPos blockpos = null;
      RandomSource random = this.kangaroo.getRandom();
      int range = 15;

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = this.kangaroo.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

         while (this.kangaroo.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
            blockpos1 = blockpos1.below();
         }

         if (this.kangaroo.level().getFluidState(blockpos1).is(FluidTags.WATER)) {
            blockpos = blockpos1;
         }
      }

      return blockpos;
   }

   protected void checkAndPerformAttack(LivingEntity enemy) {
      this.amCheckAndPerformAttack(enemy, this.mob.distanceToSqr(enemy.getX(), enemy.getY(), enemy.getZ()));
   }

   protected void amCheckAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      double d0 = AMPlatform.attackReachSqr(this.mob, enemy) + 5.0;
      if (distToEnemySqr <= d0) {
         if (this.kangaroo.isInWater()) {
            float f1 = this.kangaroo.getYRot() * 0.017453292F;
            this.kangaroo.setDeltaMovement(this.kangaroo.getDeltaMovement().add(-Mth.sin(f1) * 0.3F, 0.0, Mth.cos(f1) * 0.3F));
            AMCompat.knockback(enemy, 1.0, enemy.getX() - this.kangaroo.getX(), enemy.getZ() - this.kangaroo.getZ());
         }

         this.resetAttackCooldown();
         if (this.kangaroo.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            if (this.kangaroo.getMainHandItem().is(AMTagRegistry.KANGAROO_SPEARS)) {
               this.kangaroo.setAnimation(this.kangaroo.isLeftHanded() ? EntityKangaroo.ANIMATION_STAB_L : EntityKangaroo.ANIMATION_STAB_R);
            } else if (this.kangaroo.getRandom().nextBoolean()) {
               this.kangaroo.setAnimation(EntityKangaroo.ANIMATION_KICK);
            } else if (!this.kangaroo.getMainHandItem().isEmpty()) {
               this.kangaroo.setAnimation(this.kangaroo.isLeftHanded() ? EntityKangaroo.ANIMATION_PUNCH_L : EntityKangaroo.ANIMATION_PUNCH_R);
            } else {
               this.kangaroo.setAnimation(this.kangaroo.getRandom().nextBoolean() ? EntityKangaroo.ANIMATION_PUNCH_R : EntityKangaroo.ANIMATION_PUNCH_L);
            }
         }
      }
   }
}
