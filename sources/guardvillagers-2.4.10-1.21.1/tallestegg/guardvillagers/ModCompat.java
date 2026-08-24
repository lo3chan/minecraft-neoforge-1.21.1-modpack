package tallestegg.guardvillagers;

import ewewukek.musketmod.GunItem;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import tallestegg.guardvillagers.common.entities.Guard;

public class ModCompat {
   public static ArmPose reloadMusketAnim(ItemStack stack, InteractionHand handIn, Guard guard, ArmPose bipedmodel$armpose) {
      return stack.getItem() instanceof GunItem && !GunItem.isLoaded(stack) && handIn == guard.getUsedItemHand() ? ArmPose.CROSSBOW_CHARGE : bipedmodel$armpose;
   }

   public static boolean isHoldingMusket(ItemStack stack) {
      return stack.getItem() instanceof GunItem;
   }

   public static ArmPose holdMusketAnim(ItemStack stack, Guard guard) {
      return stack.getItem() instanceof GunItem && GunItem.isLoaded(stack) && guard.isAggressive() ? ArmPose.CROSSBOW_HOLD : ArmPose.ITEM;
   }

   public static void shootGun(Guard guard) {
      if (guard.getMainHandItem().getItem() instanceof GunItem musketItem) {
         Vec3 front = Vec3.directionFromRotation(guard.getXRot(), guard.getYRot());
         musketItem.fire(guard, front);
         GunItem.setLoaded(guard.getMainHandItem(), false);
         guard.playSound(musketItem.fireSound(), 3.5F, 1.0F);
         guard.damageGuardItem(1, EquipmentSlot.MAINHAND, guard.getMainHandItem());
      }
   }

   public static class UseMusketGoal<T extends PathfinderMob & RangedAttackMob> extends Goal {
      private final float attackRadiusSqr;
      private final T mob;
      private int attackIntervalMin;
      private Path path;
      private int attackTime = -1;
      private int seeTime;
      private int timeUntilShoot = 20;

      public UseMusketGoal(T pMob, int pAttackIntervalMin, float pAttackRadius) {
         this.mob = pMob;
         this.attackIntervalMin = pAttackIntervalMin;
         this.attackRadiusSqr = pAttackRadius * pAttackRadius;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.mob.getTarget();
         return target != null && this.mob.getMainHandItem().getItem() instanceof GunItem;
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public void start() {
         this.mob.setAggressive(true);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity target = this.mob.getTarget();
         if (target != null) {
            double distanceSquared = this.mob.distanceToSqr(target);
            boolean canSee = this.mob.getSensing().hasLineOfSight(target);
            boolean seeTimeGreaterThanZero = this.seeTime > 0;
            this.mob.getLookControl().setLookAt(target);
            this.mob.lookAt(target, 30.0F, 30.0F);
            if (!canSee && this.seeTime < -60) {
               this.mob.stopUsingItem();
            }

            if (GunItem.isLoaded(this.mob.getMainHandItem())) {
               this.mob.stopUsingItem();
               if (canSee) {
                  this.timeUntilShoot--;
                  if (this.timeUntilShoot <= 0) {
                     this.mob.performRangedAttack(target, ((GunItem)this.mob.getMainHandItem().getItem()).bulletSpeed());
                     this.attackTime = this.attackIntervalMin;
                  }
               }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60 && !GunItem.isLoaded(this.mob.getMainHandItem())) {
               this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof GunItem));
               this.timeUntilShoot = 20;
            }

            if (canSee != seeTimeGreaterThanZero) {
               this.seeTime = 0;
            }

            if (canSee) {
               this.seeTime++;
            } else {
               this.seeTime--;
            }

            if (distanceSquared <= 6.0) {
               this.mob.getMoveControl().strafe(-0.5F, 0.0F);
            }

            if (distanceSquared > this.attackRadiusSqr || this.seeTime < 5) {
               this.mob.getNavigation().moveTo(target, 1.0);
            } else if (distanceSquared < this.attackRadiusSqr) {
               this.mob.getNavigation().stop();
            }

            if (Guard.RangedCrossbowAttackPassiveGoal.friendlyInLineOfSight(this.mob)) {
               Vec3 vec3 = this.getPosition(this.mob);
               if (distanceSquared <= this.attackRadiusSqr && vec3 != null && this.mob.getNavigation().isDone()) {
                  this.path = this.mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                  this.mob.getLookControl().setLookAt(vec3.x, this.mob.getEyeY(), vec3.z);
                  if (this.path != null && this.path.canReach()) {
                     this.mob.getNavigation().moveTo(this.path, 0.9);
                     this.attackTime = -1;
                     this.mob.stopUsingItem();
                  }
               }
            }
         }
      }

      public void stop() {
         this.mob.setAggressive(false);
         this.seeTime = 0;
         this.attackTime = -1;
         this.mob.stopUsingItem();
         this.timeUntilShoot = 20;
      }

      @Nullable
      protected Vec3 getPosition(T mob) {
         return mob.getTarget() != null ? LandRandomPos.getPosAway(mob, 5, 7, mob.getTarget().position()) : LandRandomPos.getPos(mob, 5, 7);
      }
   }
}
