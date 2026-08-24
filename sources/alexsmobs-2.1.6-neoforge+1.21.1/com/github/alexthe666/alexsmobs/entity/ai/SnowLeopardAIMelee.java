package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class SnowLeopardAIMelee extends Goal {
   private final EntitySnowLeopard leopard;
   private LivingEntity target;
   private boolean secondPartOfLeap = false;
   private Vec3 leapPos = null;
   private int jumpCooldown = 0;
   private boolean stalk = false;

   public SnowLeopardAIMelee(EntitySnowLeopard snowLeopard) {
      this.leopard = snowLeopard;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   @Nullable
   private static BlockPos getRandomDelta(
      RandomSource p_226343_0_, int p_226343_1_, int p_226343_2_, int p_226343_3_, @Nullable Vec3 p_226343_4_, double p_226343_5_
   ) {
      if (p_226343_4_ != null && p_226343_5_ < 3.141592653589793) {
         double lvt_7_2_ = Mth.atan2(p_226343_4_.z, p_226343_4_.x) - 1.5707963705062866;
         double lvt_9_2_ = lvt_7_2_ + (2.0F * p_226343_0_.nextFloat() - 1.0F) * p_226343_5_;
         double lvt_11_1_ = Math.sqrt(p_226343_0_.nextDouble()) * Mth.SQRT_OF_TWO * p_226343_1_;
         double lvt_13_1_ = -lvt_11_1_ * Math.sin(lvt_9_2_);
         double lvt_15_1_ = lvt_11_1_ * Math.cos(lvt_9_2_);
         if (Math.abs(lvt_13_1_) <= p_226343_1_ && Math.abs(lvt_15_1_) <= p_226343_1_) {
            int lvt_17_1_ = p_226343_0_.nextInt(2 * p_226343_2_ + 1) - p_226343_2_ + p_226343_3_;
            return AMBlockPos.fromCoords(lvt_13_1_, lvt_17_1_, lvt_15_1_);
         } else {
            return null;
         }
      } else {
         int lvt_7_1_ = p_226343_0_.nextInt(2 * p_226343_1_ + 1) - p_226343_1_;
         int lvt_8_1_ = p_226343_0_.nextInt(2 * p_226343_2_ + 1) - p_226343_2_ + p_226343_3_;
         int lvt_9_1_ = p_226343_0_.nextInt(2 * p_226343_1_ + 1) - p_226343_1_;
         return new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_);
      }
   }

   public static BlockPos moveUpToAboveSolid(BlockPos p_226342_0_, int p_226342_1_, int p_226342_2_, Predicate<BlockPos> p_226342_3_) {
      if (p_226342_1_ < 0) {
         throw new IllegalArgumentException("aboveSolidAmount was " + p_226342_1_ + ", expected >= 0");
      } else if (!p_226342_3_.test(p_226342_0_)) {
         return p_226342_0_;
      } else {
         BlockPos lvt_4_1_ = p_226342_0_.above();

         while (lvt_4_1_.getY() < p_226342_2_ && p_226342_3_.test(lvt_4_1_)) {
            lvt_4_1_ = lvt_4_1_.above();
         }

         BlockPos lvt_5_1_ = lvt_4_1_;

         while (lvt_5_1_.getY() < p_226342_2_ && lvt_5_1_.getY() - lvt_4_1_.getY() < p_226342_1_) {
            BlockPos lvt_6_1_ = lvt_5_1_.above();
            if (p_226342_3_.test(lvt_6_1_)) {
               break;
            }

            lvt_5_1_ = lvt_6_1_;
         }

         return lvt_5_1_;
      }
   }

   public boolean canUse() {
      return this.leopard.getTarget() != null
         && !this.leopard.isSleeping()
         && !this.leopard.isSitting()
         && (this.leopard.getTarget().isAlive() || this.leopard.getTarget() instanceof Player)
         && !this.leopard.isBaby();
   }

   public void start() {
      this.target = this.leopard.getTarget();
      if (this.target instanceof Player && this.leopard.getLastHurtByMob() != null && this.leopard.getLastHurtByMob() == this.target) {
         this.stalk = this.leopard.distanceTo(this.target) > 10.0F;
      } else {
         this.stalk = this.leopard.distanceTo(this.target) > 4.0F;
      }

      this.secondPartOfLeap = false;
   }

   public void stop() {
      this.secondPartOfLeap = false;
      this.stalk = false;
      this.leapPos = null;
      this.jumpCooldown = 0;
      this.leopard.setTackling(false);
      this.leopard.setSlSneaking(false);
   }

   public void tick() {
      if (this.jumpCooldown > 0) {
         this.jumpCooldown--;
      }

      if (this.stalk) {
         if (this.secondPartOfLeap) {
            this.leopard.setTackling(!this.leopard.onGround());
            this.leopard.lookAt(this.target, 180.0F, 10.0F);
            this.leopard.yBodyRot = this.leopard.getYRot();
            if (this.leopard.distanceTo(this.target) < 3.0F && this.leopard.hasLineOfSight(this.target)) {
               this.target
                  .hurt(this.leopard.damageSources().mobAttack(this.leopard), (float)(this.leopard.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2.5));
               this.stalk = false;
               this.secondPartOfLeap = false;
            } else if (this.leopard.onGround() && this.jumpCooldown == 0) {
               this.leopard.setSlSneaking(false);
               this.jumpCooldown = 10 + this.leopard.getRandom().nextInt(10);
               Vec3 vector3d = this.leopard.getDeltaMovement();
               Vec3 vector3d1 = new Vec3(this.target.getX() - this.leopard.getX(), 0.0, this.target.getZ() - this.leopard.getZ());
               if (vector3d1.lengthSqr() > 1.0E-7) {
                  vector3d1 = vector3d1.normalize().scale(0.9).add(vector3d.scale(0.8));
               }

               this.leopard.setDeltaMovement(vector3d1.x, vector3d1.y + 0.6000000238418579, vector3d1.z);
            }
         } else if (this.leapPos != null && !(this.target.distanceToSqr(this.leapPos) > 250.0)) {
            this.leopard.setSlSneaking(true);
            this.leopard.getNavigation().moveTo(this.leapPos.x, this.leapPos.y, this.leapPos.z, 1.0);
            if (this.leopard.distanceToSqr(this.leapPos.x, this.leapPos.y, this.leapPos.z) < 9.0 && this.leopard.hasLineOfSight(this.target)) {
               this.secondPartOfLeap = true;
               this.leopard.getNavigation().stop();
            }
         } else {
            Vec3 vector3d1 = this.calculateFarPoint(50.0);
            if (vector3d1 != null) {
               this.leapPos = vector3d1;
            } else {
               this.leapPos = LandRandomPos.getPosTowards(this.leopard, 10, 10, this.target.position());
            }
         }
      } else {
         this.leopard.setSlSneaking(false);
         this.leopard.getNavigation().moveTo(this.target, 1.0);
         if (this.leopard.distanceTo(this.target) < 3.0F) {
            if (this.leopard.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               this.leopard.setAnimation(this.leopard.getRandom().nextBoolean() ? EntitySnowLeopard.ANIMATION_ATTACK_R : EntitySnowLeopard.ANIMATION_ATTACK_L);
            } else if (this.leopard.getAnimationTick() == 5) {
               AMCompat.doHurtTarget(this.leopard, this.target);
            }
         }
      }
   }

   private Vec3 calculateFarPoint(double dist) {
      Vec3 highest = null;

      for (int i = 0; i < 10; i++) {
         Vec3 vector3d1 = this.calculateVantagePoint(
            this.target,
            8,
            3,
            1,
            this.target.position().subtract(this.leopard.getX(), this.leopard.getY(), this.leopard.getZ()),
            false,
            1.5707963705062866,
            this.leopard::getWalkTargetValue,
            false,
            0,
            0,
            true
         );
         if (vector3d1 != null && this.target.distanceToSqr(vector3d1) > dist && (highest == null || highest.y() < vector3d1.y)) {
            highest = vector3d1;
         }
      }

      return highest;
   }

   @Nullable
   private Vec3 calculateVantagePoint(
      LivingEntity creature,
      int xz,
      int y,
      int p_226339_3_,
      @Nullable Vec3 p_226339_4_,
      boolean p_226339_5_,
      double p_226339_6_,
      ToDoubleFunction<BlockPos> p_226339_8_,
      boolean p_226339_9_,
      int p_226339_10_,
      int p_226339_11_,
      boolean p_226339_12_
   ) {
      PathNavigation lvt_13_1_ = this.leopard.getNavigation();
      RandomSource lvt_14_1_ = creature.getRandom();
      boolean lvt_15_2_;
      if (this.leopard.hasRestriction()) {
         lvt_15_2_ = this.leopard.getRestrictCenter().closerToCenterThan(creature.position(), this.leopard.getRestrictRadius() + xz + 1.0);
      } else {
         lvt_15_2_ = false;
      }

      boolean lvt_16_1_ = false;
      double lvt_17_1_ = -1.0 / 0.0;
      BlockPos lvt_19_1_ = creature.blockPosition();

      for (int lvt_20_1_ = 0; lvt_20_1_ < 10; lvt_20_1_++) {
         BlockPos lvt_21_1_ = getRandomDelta(lvt_14_1_, xz, y, p_226339_3_, p_226339_4_, p_226339_6_);
         if (lvt_21_1_ != null) {
            int lvt_22_1_ = lvt_21_1_.getX();
            int lvt_23_1_ = lvt_21_1_.getY();
            int lvt_24_1_ = lvt_21_1_.getZ();
            if (this.leopard.hasRestriction() && xz > 1) {
               BlockPos lvt_25_2_ = this.leopard.getRestrictCenter();
               if (creature.getX() > lvt_25_2_.getX()) {
                  lvt_22_1_ -= lvt_14_1_.nextInt(xz / 2);
               } else {
                  lvt_22_1_ += lvt_14_1_.nextInt(xz / 2);
               }

               if (creature.getZ() > lvt_25_2_.getZ()) {
                  lvt_24_1_ -= lvt_14_1_.nextInt(xz / 2);
               } else {
                  lvt_24_1_ += lvt_14_1_.nextInt(xz / 2);
               }
            }

            BlockPos lvt_25_2_x = AMBlockPos.fromCoords(lvt_22_1_ + creature.getX(), lvt_23_1_ + creature.getY(), lvt_24_1_ + creature.getZ());
            if (lvt_25_2_x.getY() >= 0
               && lvt_25_2_x.getY() <= AMCompat.maxBuildHeight(creature.level())
               && (!lvt_15_2_ || this.leopard.isWithinRestriction(lvt_25_2_x))
               && (!p_226339_12_ || lvt_13_1_.isStableDestination(lvt_25_2_x))) {
               if (p_226339_9_) {
                  lvt_25_2_x = moveUpToAboveSolid(
                     lvt_25_2_x,
                     lvt_14_1_.nextInt(p_226339_10_ + 1) + p_226339_11_,
                     AMCompat.maxBuildHeight(creature.level()),
                     p_226341_1_ -> creature.level().getBlockState(p_226341_1_).isSolid()
                  );
               }

               if (p_226339_5_ || !creature.level().getFluidState(lvt_25_2_x).is(FluidTags.WATER)) {
                  PathType lvt_26_1_ = AMCompat.pathTypeStatic(this.leopard, lvt_25_2_x);
                  if (this.leopard.getPathfindingMalus(lvt_26_1_) == 0.0F) {
                     double lvt_27_1_ = p_226339_8_.applyAsDouble(lvt_25_2_x);
                     if (lvt_27_1_ > lvt_17_1_) {
                        lvt_17_1_ = lvt_27_1_;
                        lvt_19_1_ = lvt_25_2_x;
                        lvt_16_1_ = true;
                     }
                  }
               }
            }
         }
      }

      return lvt_16_1_ ? Vec3.atBottomCenterOf(lvt_19_1_) : null;
   }
}
