package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class RaccoonAIWash extends Goal {
   private final EntityRaccoon raccoon;
   private BlockPos waterPos;
   private BlockPos targetPos;
   private int washTime = 0;
   private int executionChance = 30;
   private Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public RaccoonAIWash(EntityRaccoon creature) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.raccoon = creature;
   }

   public boolean canUse() {
      if (this.raccoon.getMainHandItem().isEmpty()) {
         return false;
      } else {
         if (this.raccoon.lookForWaterBeforeEatingTimer > 0) {
            this.waterPos = this.generateTarget();
            if (this.waterPos != null) {
               this.targetPos = this.getLandPos(this.waterPos);
               return this.targetPos != null;
            }
         }

         return false;
      }
   }

   public void start() {
      this.raccoon.lookForWaterBeforeEatingTimer = 1800;
   }

   public void stop() {
      this.targetPos = null;
      this.waterPos = null;
      this.washTime = 0;
      this.raccoon.setWashPos(null);
      this.raccoon.setWashing(false);
      this.raccoon.lookForWaterBeforeEatingTimer = 100;
      this.raccoon.getNavigation().stop();
   }

   public void tick() {
      if (this.targetPos != null && this.waterPos != null) {
         double dist = this.raccoon.distanceToSqr(Vec3.atCenterOf(this.waterPos));
         if (dist > 2.0 && this.raccoon.isWashing()) {
            this.raccoon.setWashing(false);
         }

         if (dist <= 1.0) {
            double d0 = this.waterPos.getX() + 0.5 - this.raccoon.getX();
            double d2 = this.waterPos.getZ() + 0.5 - this.raccoon.getZ();
            float yaw = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
            this.raccoon.setYRot(yaw);
            this.raccoon.yHeadRot = yaw;
            this.raccoon.yBodyRot = yaw;
            this.raccoon.getNavigation().stop();
            this.raccoon.setWashing(true);
            this.raccoon.setWashPos(this.waterPos);
            this.raccoon.lookForWaterBeforeEatingTimer = 0;
            if (this.washTime % 10 == 0) {
               this.raccoon.gameEvent(GameEvent.BLOCK_ACTIVATE);
               this.raccoon.playSound(SoundEvents.GENERIC_SWIM, 0.7F, 0.5F + this.raccoon.getRandom().nextFloat());
            }

            this.washTime++;
            if (this.washTime > 100 || this.raccoon.isHoldingSugar() && this.washTime > 20) {
               this.stop();
               if (!this.raccoon.isHoldingSugar()) {
                  this.raccoon.onEatItem();
               }

               this.raccoon.postWashItem(this.raccoon.getMainHandItem());
               if (AMCompat.hasCraftingRemainder(this.raccoon.getMainHandItem())) {
                  AMCompat.spawnAtLocation(this.raccoon, AMCompat.craftingRemainder(this.raccoon.getMainHandItem()));
               }

               this.raccoon.getMainHandItem().shrink(1);
            }
         } else {
            this.raccoon.getNavigation().moveTo(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ(), 1.2);
         }
      }
   }

   public boolean canContinueToUse() {
      return this.raccoon.getMainHandItem().isEmpty()
         ? false
         : this.targetPos != null && !this.raccoon.isInWater() && EntityRaccoon.isRaccoonFood(this.raccoon.getMainHandItem());
   }

   public BlockPos generateTarget() {
      BlockPos blockpos = null;
      RandomSource random = this.raccoon.getRandom();
      int range = 32;

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = this.raccoon.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

         while (this.raccoon.level().isEmptyBlock(blockpos1) && blockpos1.getY() > AMCompat.minBuildHeight(this.raccoon.level())) {
            blockpos1 = blockpos1.below();
         }

         if (this.isConnectedToLand(blockpos1)) {
            blockpos = blockpos1;
         }
      }

      return blockpos;
   }

   public boolean isConnectedToLand(BlockPos pos) {
      if (this.raccoon.level().getFluidState(pos).is(FluidTags.WATER)) {
         for (Direction dir : this.HORIZONTALS) {
            BlockPos offsetPos = pos.relative(dir);
            if (this.raccoon.level().getFluidState(offsetPos).isEmpty() && this.raccoon.level().getFluidState(offsetPos.above()).isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   public BlockPos getLandPos(BlockPos pos) {
      if (this.raccoon.level().getFluidState(pos).is(FluidTags.WATER)) {
         for (Direction dir : this.HORIZONTALS) {
            BlockPos offsetPos = pos.relative(dir);
            if (this.raccoon.level().getFluidState(offsetPos).isEmpty() && this.raccoon.level().getFluidState(offsetPos.above()).isEmpty()) {
               return offsetPos;
            }
         }
      }

      return null;
   }
}
