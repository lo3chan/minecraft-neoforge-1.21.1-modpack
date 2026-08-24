package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class AnimalAILeaveWater extends Goal {
   private final PathfinderMob creature;
   private BlockPos targetPos;
   private final int executionChance = 30;

   public AnimalAILeaveWater(PathfinderMob creature) {
      this.creature = creature;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.creature.level().getFluidState(this.creature.blockPosition()).is(FluidTags.WATER)
         && (this.creature.getTarget() != null || this.creature.getRandom().nextInt(30) == 0)
         && this.creature instanceof ISemiAquatic
         && ((ISemiAquatic)this.creature).shouldLeaveWater()) {
         this.targetPos = this.generateTarget();
         return this.targetPos != null;
      } else {
         return false;
      }
   }

   public void start() {
      if (this.targetPos != null) {
         this.creature.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
      }
   }

   public void tick() {
      if (this.targetPos != null) {
         this.creature.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
      }

      if (this.creature.horizontalCollision && this.creature.isInWater()) {
         float f1 = this.creature.getYRot() * 0.017453292F;
         this.creature.setDeltaMovement(this.creature.getDeltaMovement().add(-Mth.sin(f1) * 0.2F, 0.1, Mth.cos(f1) * 0.2F));
      }
   }

   public boolean canContinueToUse() {
      if (this.creature instanceof ISemiAquatic && !((ISemiAquatic)this.creature).shouldLeaveWater()) {
         this.creature.getNavigation().stop();
         return false;
      } else {
         return !this.creature.getNavigation().isDone() && this.targetPos != null && !this.creature.level().getFluidState(this.targetPos).is(FluidTags.WATER);
      }
   }

   public BlockPos generateTarget() {
      Vec3 vector3d = LandRandomPos.getPos(this.creature, 23, 7);

      for (int tries = 0; vector3d != null && tries < 8; tries++) {
         boolean waterDetected = false;
         Iterator var4 = BlockPos.betweenClosed(
               Mth.floor(vector3d.x - 2.0),
               Mth.floor(vector3d.y - 1.0),
               Mth.floor(vector3d.z - 2.0),
               Mth.floor(vector3d.x + 2.0),
               Mth.floor(vector3d.y),
               Mth.floor(vector3d.z + 2.0)
            )
            .iterator();

         while (true) {
            if (var4.hasNext()) {
               BlockPos blockpos1 = (BlockPos)var4.next();
               if (!this.creature.level().getFluidState(blockpos1).is(FluidTags.WATER)) {
                  continue;
               }

               waterDetected = true;
            }

            if (!waterDetected) {
               return AMBlockPos.fromVec3(vector3d);
            }

            vector3d = LandRandomPos.getPos(this.creature, 23, 7);
            break;
         }
      }

      return null;
   }
}
