package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class PlatypusAIDigForItems extends Goal {
   public static final ResourceLocation PLATYPUS_REWARD = AMCompat.rl("alexsmobs", "gameplay/platypus_reward");
   public static final ResourceLocation PLATYPUS_REWARD_CHARGED = AMCompat.rl("alexsmobs", "gameplay/platypus_supercharged_reward");
   private EntityPlatypus platypus;
   private BlockPos digPos;
   private int generatePosCooldown = 0;
   private int digTime = 0;
   private int maxDroppedItems = 3;

   public PlatypusAIDigForItems(EntityPlatypus platypus) {
      this.platypus = platypus;
   }

   private static List<ItemStack> getItemStacks(EntityPlatypus platypus) {
      LootTable loottable = AMCompat.lootTable(platypus.level().getServer(), platypus.superCharged ? PLATYPUS_REWARD_CHARGED : PLATYPUS_REWARD);
      return loottable.getRandomItems(
         new Builder((ServerLevel)platypus.level()).withParameter(LootContextParams.THIS_ENTITY, platypus).create(LootContextParamSets.PIGLIN_BARTER)
      );
   }

   public boolean canUse() {
      if (!this.platypus.isSensing()) {
         return false;
      } else if (this.generatePosCooldown == 0) {
         this.generatePosCooldown = 20 + this.platypus.getRandom().nextInt(20);
         this.digPos = this.genDigPos();
         this.maxDroppedItems = 2 + this.platypus.getRandom().nextInt(5);
         return this.digPos != null;
      } else {
         this.generatePosCooldown--;
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.platypus.getTarget() == null
         && this.platypus.isSensing()
         && this.platypus.getLastHurtByMob() == null
         && this.digPos != null
         && this.platypus.level().getBlockState(this.digPos).is(AMTagRegistry.PLATYPUS_DIGABLES)
         && this.platypus.level().getFluidState(this.digPos.above()).is(FluidTags.WATER);
   }

   public void tick() {
      double dist = this.platypus.distanceToSqr(Vec3.atCenterOf(this.digPos.above()));
      double d0 = this.digPos.getX() + 0.5 - this.platypus.getX();
      double d1 = this.digPos.getY() + 0.5 - this.platypus.getEyeY();
      double d2 = this.digPos.getZ() + 0.5 - this.platypus.getZ();
      float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
      if (dist < 2.0) {
         this.platypus.setDeltaMovement(this.platypus.getDeltaMovement().add(0.0, -0.009999999776482582, 0.0));
         this.platypus.getNavigation().stop();
         this.digTime++;
         if (this.digTime % 5 == 0) {
            SoundEvent sound = this.platypus.level().getBlockState(this.digPos).getSoundType().getHitSound();
            this.platypus.gameEvent(GameEvent.BLOCK_ACTIVATE);
            this.platypus.playSound(sound, 1.0F, 0.5F + this.platypus.getRandom().nextFloat() * 0.5F);
         }

         int itemDivis = (int)Math.floor(100.0F / this.maxDroppedItems);
         if (this.digTime % itemDivis == 0) {
            List<ItemStack> lootList = getItemStacks(this.platypus);
            if (lootList.size() > 0) {
               for (ItemStack stack : lootList) {
                  ItemEntity e = AMCompat.spawnAtLocation(this.platypus, stack.copy());
                  e.hasImpulse = true;
                  e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
               }
            }
         }

         if (this.digTime >= 100) {
            this.platypus.setSensing(false);
            this.platypus.setDigging(false);
            this.digTime = 0;
         } else {
            this.platypus.setDigging(true);
         }
      } else {
         this.platypus.setDigging(false);
         this.platypus.getNavigation().moveTo(this.digPos.getX(), this.digPos.getY() + 1, this.digPos.getZ(), 1.0);
         this.platypus.setYRot(f);
      }
   }

   public void stop() {
      this.generatePosCooldown = 0;
      this.platypus.setSensing(false);
      this.platypus.setDigging(false);
      this.digPos = null;
      this.digTime = 0;
   }

   private BlockPos genSeafloorPos(BlockPos parent) {
      LevelAccessor world = this.platypus.level();
      RandomSource random = this.platypus.getRandom();
      int range = 15;

      for (int i = 0; i < 15; i++) {
         BlockPos seafloor = parent.offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);

         while (world.getFluidState(seafloor).is(FluidTags.WATER) && seafloor.getY() > 1) {
            seafloor = seafloor.below();
         }

         BlockState state = world.getBlockState(seafloor);
         if (state.is(AMTagRegistry.PLATYPUS_DIGABLES)) {
            return seafloor;
         }
      }

      return null;
   }

   private BlockPos genDigPos() {
      RandomSource random = this.platypus.getRandom();
      int range = 15;
      if (this.platypus.isInWater()) {
         return this.genSeafloorPos(this.platypus.blockPosition());
      } else {
         for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.platypus.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

            while (this.platypus.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
               blockpos1 = blockpos1.below();
            }

            if (this.platypus.level().getFluidState(blockpos1).is(FluidTags.WATER)) {
               BlockPos pos3 = this.genSeafloorPos(blockpos1);
               if (pos3 != null) {
                  return pos3;
               }
            }
         }

         return null;
      }
   }
}
