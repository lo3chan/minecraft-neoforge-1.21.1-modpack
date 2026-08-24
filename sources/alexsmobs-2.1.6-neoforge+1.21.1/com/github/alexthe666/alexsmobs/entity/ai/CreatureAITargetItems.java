package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ITargetsDroppedItems;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CreatureAITargetItems<T extends ItemEntity> extends TargetGoal {
   protected final CreatureAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super ItemEntity> targetEntitySelector;
   protected int executionChance;
   protected boolean mustUpdate;
   protected ItemEntity targetEntity;
   protected ITargetsDroppedItems hunter;
   private final int tickThreshold;
   private float radius = 9.0F;
   private int walkCooldown = 0;

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight) {
      this(creature, checkSight, false);
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, int tickThreshold) {
      this(creature, checkSight, false, tickThreshold, 9);
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby) {
      this(creature, 10, checkSight, onlyNearby, null, 0);
   }

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
      this(creature, 10, checkSight, onlyNearby, null, tickThreshold);
      this.radius = radius;
   }

   public CreatureAITargetItems(
      PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<? super T> targetSelector, int ticksExisted
   ) {
      super(creature, checkSight, onlyNearby);
      this.executionChance = chance;
      this.tickThreshold = ticksExisted;
      this.hunter = (ITargetsDroppedItems)creature;
      this.theNearestAttackableTargetSorter = new CreatureAITargetItems.Sorter(creature);
      this.targetEntitySelector = new Predicate<ItemEntity>() {
         public boolean apply(@Nullable ItemEntity item) {
            ItemStack stack = item.getItem();
            return !stack.isEmpty() && CreatureAITargetItems.this.hunter.canTargetItem(stack) && item.tickCount > CreatureAITargetItems.this.tickThreshold;
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
               this.mustUpdate = false;
               this.hunter.onFindTarget(this.targetEntity);
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
      AABB aabb = new AABB(-this.radius, -this.radius, -this.radius, this.radius, this.radius, this.radius);
      return aabb.move(renderCenter);
   }

   public void start() {
      this.moveTo();
      super.start();
   }

   protected void moveTo() {
      if (this.walkCooldown > 0) {
         this.walkCooldown--;
      } else {
         this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
         this.walkCooldown = 30 + this.mob.getRandom().nextInt(40);
      }
   }

   public void stop() {
      super.stop();
      this.mob.getNavigation().stop();
      this.targetEntity = null;
   }

   public void tick() {
      super.tick();
      if (this.targetEntity != null && (this.targetEntity == null || this.targetEntity.isAlive())) {
         this.moveTo();
      } else {
         this.stop();
         this.mob.getNavigation().stop();
      }

      if (this.targetEntity != null && this.mob.hasLineOfSight(this.targetEntity) && this.mob.getBbWidth() > 2.0 && this.mob.onGround()) {
         this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
      }

      if (this.targetEntity != null
         && this.targetEntity.isAlive()
         && this.mob.distanceToSqr(this.targetEntity) < this.hunter.getMaxDistToItem()
         && this.mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
         this.hunter.onGetItem(this.targetEntity);
         this.targetEntity.getItem().shrink(1);
         this.stop();
      }
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public boolean canContinueToUse() {
      boolean path = this.mob.getBbWidth() > 2.0 || !this.mob.getNavigation().isDone();
      return path && this.targetEntity != null && this.targetEntity.isAlive();
   }

   public record Sorter(Entity theEntity) implements Comparator<Entity> {
      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.theEntity.distanceToSqr(p_compare_1_);
         double d1 = this.theEntity.distanceToSqr(p_compare_2_);
         return Double.compare(d0, d1);
      }
   }
}
