package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.Vec3;

public class ShoebillAIFish extends Goal {
   private final EntityShoebill bird;
   private BlockPos waterPos = null;
   private BlockPos targetPos = null;
   private int executionChance = 0;
   private final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private int idleTime = 0;
   private int navigateTime = 0;

   public ShoebillAIFish(EntityShoebill bird) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.bird = bird;
   }

   public void stop() {
      this.targetPos = null;
      this.waterPos = null;
      this.idleTime = 0;
      this.navigateTime = 0;
      this.bird.getNavigation().stop();
   }

   public void tick() {
      if (this.targetPos != null && this.waterPos != null) {
         double dist = this.bird.distanceToSqr(Vec3.atCenterOf(this.waterPos));
         if (dist <= 1.0) {
            this.navigateTime = 0;
            double d0 = this.waterPos.getX() + 0.5 - this.bird.getX();
            double d2 = this.waterPos.getZ() + 0.5 - this.bird.getZ();
            float yaw = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
            this.bird.setYRot(yaw);
            this.bird.yHeadRot = yaw;
            this.bird.yBodyRot = yaw;
            this.bird.getNavigation().stop();
            this.idleTime++;
            if (this.idleTime > 25) {
               this.bird.setAnimation(EntityShoebill.ANIMATION_FISH);
            }

            if (this.idleTime > 45 && this.bird.getAnimation() == EntityShoebill.ANIMATION_FISH) {
               this.bird.gameEvent(GameEvent.ITEM_INTERACT_START);
               this.bird.playSound(SoundEvents.GENERIC_SPLASH, 0.7F, 0.5F + this.bird.getRandom().nextFloat());
               this.bird.resetFishingCooldown();
               this.spawnFishingLoot();
               this.stop();
            }
         } else {
            this.navigateTime++;
            this.bird.getNavigation().moveTo(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ(), 1.2);
         }

         if (this.navigateTime > 3600) {
            this.stop();
         }
      }
   }

   public boolean canContinueToUse() {
      return this.targetPos != null && this.bird.fishingCooldown == 0 && this.bird.revengeCooldown == 0 && !this.bird.isFlying();
   }

   public void spawnFishingLoot() {
      double luck = 0.0 + this.bird.luckLevel * 0.5F;
      Builder lootcontext$builder = new Builder((ServerLevel)this.bird.level());
      lootcontext$builder.withLuck((float)luck);
      net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder lootparameterset$builder = new net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder();
      LootTable loottable = AMCompat.fishingLoot(this.bird.level().getServer());

      for (ItemStack itemstack : loottable.getRandomItems(lootcontext$builder.create(lootparameterset$builder.build()))) {
         ItemEntity item = new ItemEntity(this.bird.level(), this.bird.getX() + 0.5, this.bird.getY(), this.bird.getZ(), itemstack);
         if (!this.bird.level().isClientSide()) {
            this.bird.level().addFreshEntity(item);
         }
      }
   }

   public boolean canUse() {
      if (!this.bird.isFlying() && this.bird.fishingCooldown == 0 && this.bird.getRandom().nextInt(30) == 0) {
         if (this.bird.isInWater()) {
            this.waterPos = this.bird.blockPosition();
            this.targetPos = this.waterPos;
            return true;
         }

         this.waterPos = this.generateTarget();
         if (this.waterPos != null) {
            this.targetPos = this.getLandPos(this.waterPos);
            return this.targetPos != null;
         }
      }

      return false;
   }

   public BlockPos generateTarget() {
      BlockPos blockpos = null;
      RandomSource random = this.bird.getRandom();
      int range = 32;

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = this.bird.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

         while (this.bird.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
            blockpos1 = blockpos1.below();
         }

         if (this.isConnectedToLand(blockpos1)) {
            blockpos = blockpos1;
         }
      }

      return blockpos;
   }

   public boolean isConnectedToLand(BlockPos pos) {
      if (this.bird.level().getFluidState(pos).is(FluidTags.WATER)) {
         for (Direction dir : this.HORIZONTALS) {
            BlockPos offsetPos = pos.relative(dir);
            if (this.bird.level().getFluidState(offsetPos).isEmpty() && this.bird.level().getFluidState(offsetPos.above()).isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   public BlockPos getLandPos(BlockPos pos) {
      if (this.bird.level().getFluidState(pos).is(FluidTags.WATER)) {
         for (Direction dir : this.HORIZONTALS) {
            BlockPos offsetPos = pos.relative(dir);
            if (this.bird.level().getFluidState(offsetPos).isEmpty() && this.bird.level().getFluidState(offsetPos.above()).isEmpty()) {
               return offsetPos;
            }
         }
      }

      return null;
   }
}
