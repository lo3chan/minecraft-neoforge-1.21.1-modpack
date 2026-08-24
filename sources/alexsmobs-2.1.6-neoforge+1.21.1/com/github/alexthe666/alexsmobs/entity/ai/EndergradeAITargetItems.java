package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EndergradeAITargetItems<T extends ItemEntity> extends TargetGoal {
   protected final EndergradeAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super ItemEntity> targetEntitySelector;
   protected int executionChance;
   protected boolean mustUpdate;
   protected ItemEntity targetEntity;
   private EntityEndergrade endergrade;

   public EndergradeAITargetItems(EntityEndergrade creature, boolean checkSight) {
      this(creature, checkSight, false);
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public EndergradeAITargetItems(EntityEndergrade creature, boolean checkSight, boolean onlyNearby) {
      this(creature, 10, checkSight, onlyNearby, null);
   }

   public EndergradeAITargetItems(EntityEndergrade creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<? super T> targetSelector) {
      super(creature, checkSight, onlyNearby);
      this.executionChance = chance;
      this.endergrade = creature;
      this.theNearestAttackableTargetSorter = new EndergradeAITargetItems.Sorter(creature);
      this.targetEntitySelector = new Predicate<ItemEntity>() {
         public boolean apply(@Nullable ItemEntity item) {
            ItemStack stack = item.getItem();
            return !stack.isEmpty() && EndergradeAITargetItems.this.endergrade.canTargetItem(stack);
         }
      };
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.mob.isPassenger() && (!this.mob.isVehicle() || this.mob.getControllingPassenger() == null)) {
         if (!this.mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return false;
         } else {
            if (!this.mustUpdate) {
               long worldTime = this.mob.level().getGameTime() % 10L;
               if (this.mob.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<ItemEntity> list = this.mob
               .level()
               .getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.endergrade.stopWandering = true;
               this.endergrade.hasItemTarget = true;
               this.mustUpdate = false;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   protected double getFollowDistance() {
      return 16.0;
   }

   protected AABB getTargetableArea(double targetDistance) {
      Vec3 renderCenter = new Vec3(this.mob.getX() + 0.5, this.mob.getY() + 0.5, this.mob.getZ() + 0.5);
      double renderRadius = 9.0;
      AABB aabb = new AABB(-renderRadius, -renderRadius, -renderRadius, renderRadius, renderRadius, renderRadius);
      return aabb.move(renderCenter);
   }

   public void start() {
      this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
      super.start();
   }

   public void tick() {
      super.tick();
      if (this.targetEntity != null && (this.targetEntity == null || this.targetEntity.isAlive())) {
         this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
      } else {
         this.stop();
      }

      if (this.targetEntity != null
         && this.targetEntity.isAlive()
         && this.mob.distanceToSqr(this.targetEntity) < 2.0
         && this.mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
         ItemStack duplicate = this.targetEntity.getItem().copy();
         this.endergrade.bite();
         duplicate.setCount(1);
         if (!this.mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.mob.level().isClientSide()) {
            AMCompat.spawnAtLocation(this.mob, this.mob.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
         }

         this.mob.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
         this.endergrade.onGetItem(this.targetEntity);
         this.targetEntity.getItem().shrink(1);
         this.stop();
      }
   }

   public void stop() {
      this.targetEntity = null;
      this.endergrade.hasItemTarget = false;
      this.endergrade.stopWandering = false;
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public boolean canContinueToUse() {
      return this.mob.getMoveControl().hasWanted();
   }

   public record Sorter(Entity theEntity) implements Comparator<Entity> {
      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.theEntity.distanceToSqr(p_compare_1_);
         double d1 = this.theEntity.distanceToSqr(p_compare_2_);
         return Double.compare(d0, d1);
      }
   }
}
