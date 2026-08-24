package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.IHerdPanic;
import com.google.common.base.Predicate;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AnimalAIHerdPanic extends Goal {
   protected final PathfinderMob creature;
   protected final double speed;
   protected final Predicate<? super PathfinderMob> targetEntitySelector;
   protected double randPosX;
   protected double randPosY;
   protected double randPosZ;
   protected boolean running;

   public AnimalAIHerdPanic(final PathfinderMob creature, double speedIn) {
      this.creature = creature;
      this.speed = speedIn;
      this.setFlags(EnumSet.of(Flag.MOVE));
      this.targetEntitySelector = new Predicate<PathfinderMob>() {
         public boolean apply(@Nullable PathfinderMob animal) {
            return animal instanceof IHerdPanic && animal.getType() == creature.getType() ? ((IHerdPanic)animal).canPanic() : false;
         }
      };
   }

   public boolean canUse() {
      if (this.creature.getLastHurtByMob() != null && this.creature.getLastHurtByMob().isAlive()) {
         if (this.creature.isOnFire() && !this.creature.fireImmune()) {
            BlockPos blockpos = this.getRandPos(this.creature.level(), this.creature, 5, 4);
            if (blockpos != null) {
               this.randPosX = blockpos.getX();
               this.randPosY = blockpos.getY();
               this.randPosZ = blockpos.getZ();
               return true;
            }
         }

         if (this.creature.getLastHurtByMob() != null && this.creature instanceof IHerdPanic && ((IHerdPanic)this.creature).canPanic()) {
            for (PathfinderMob creatureEntity : this.creature
               .level()
               .getEntitiesOfClass(this.creature.getClass(), this.getTargetableArea(), this.targetEntitySelector)) {
               creatureEntity.setLastHurtByMob(this.creature.getLastHurtByMob());
            }

            return this.findRandomPositionFrom(this.creature.getLastHurtByMob());
         } else {
            return this.findRandomPosition();
         }
      } else {
         return false;
      }
   }

   private boolean findRandomPositionFrom(LivingEntity revengeTarget) {
      Vec3 vector3d;
      if (this.creature instanceof EntityLaviathan) {
         vector3d = DefaultRandomPos.getPosAway(this.creature, 32, 16, revengeTarget.position());
      } else {
         vector3d = LandRandomPos.getPosAway(this.creature, 16, 7, revengeTarget.position());
      }

      if (vector3d == null) {
         return false;
      } else {
         this.randPosX = vector3d.x;
         this.randPosY = vector3d.y;
         this.randPosZ = vector3d.z;
         return true;
      }
   }

   protected AABB getTargetableArea() {
      Vec3 renderCenter = new Vec3(this.creature.getX() + 0.5, this.creature.getY() + 0.5, this.creature.getZ() + 0.5);
      double searchRadius = 15.0;
      AABB aabb = new AABB(-searchRadius, -searchRadius, -searchRadius, searchRadius, searchRadius, searchRadius);
      return aabb.move(renderCenter);
   }

   protected boolean findRandomPosition() {
      Vec3 vector3d = LandRandomPos.getPos(this.creature, 5, 4);
      if (vector3d == null) {
         return false;
      } else {
         this.randPosX = vector3d.x;
         this.randPosY = vector3d.y;
         this.randPosZ = vector3d.z;
         return true;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   public void start() {
      if (this.creature instanceof IHerdPanic) {
         ((IHerdPanic)this.creature).onPanic();
      }

      this.creature.getNavigation().moveTo(this.randPosX, this.randPosY, this.randPosZ, this.speed);
      this.running = true;
   }

   public void stop() {
      this.running = false;
   }

   public boolean canContinueToUse() {
      return !this.creature.getNavigation().isDone();
   }

   @Nullable
   protected BlockPos getRandPos(BlockGetter worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
      BlockPos blockpos = entityIn.blockPosition();
      int i = blockpos.getX();
      int j = blockpos.getY();
      int k = blockpos.getZ();
      float f = horizontalRange * horizontalRange * verticalRange * 2;
      BlockPos blockpos1 = null;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int l = i - horizontalRange; l <= i + horizontalRange; l++) {
         for (int i1 = j - verticalRange; i1 <= j + verticalRange; i1++) {
            for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; j1++) {
               blockpos$mutable.set(l, i1, j1);
               if (worldIn.getFluidState(blockpos$mutable).is(FluidTags.WATER)) {
                  float f1 = (l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k);
                  if (f1 < f) {
                     f = f1;
                     blockpos1 = new BlockPos(blockpos$mutable);
                  }
               }
            }
         }
      }

      return blockpos1;
   }
}
