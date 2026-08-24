package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class SealAIDiveForItems extends Goal {
   private final EntitySeal seal;
   private Player thrower;
   private BlockPos digPos;
   private boolean returnToPlayer = false;
   private int digTime = 0;
   private int searchCooldown = 0;
   public static final ResourceLocation SEAL_REWARD = AMCompat.rl("alexsmobs", "gameplay/seal_reward");

   public SealAIDiveForItems(EntitySeal seal) {
      this.seal = seal;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   private static List<ItemStack> getItemStacks(EntitySeal seal) {
      LootTable loottable = AMCompat.lootTable(seal.level().getServer(), SEAL_REWARD);
      return loottable.getRandomItems(
         new Builder((ServerLevel)seal.level()).withParameter(LootContextParams.THIS_ENTITY, seal).create(LootContextParamSets.PIGLIN_BARTER)
      );
   }

   public boolean canUse() {
      if (this.seal.feederUUID == null || this.seal.level().getPlayerByUUID(this.seal.feederUUID) == null || this.seal.revengeCooldown > 0) {
         return false;
      } else if (this.searchCooldown > 0) {
         this.searchCooldown--;
         return false;
      } else {
         this.searchCooldown = 10;
         this.thrower = this.seal.level().getPlayerByUUID(this.seal.feederUUID);
         this.digPos = this.genDigPos();
         return this.thrower != null && this.digPos != null;
      }
   }

   public boolean canContinueToUse() {
      return this.seal.getTarget() == null
         && this.seal.revengeCooldown == 0
         && this.seal.getLastHurtByMob() == null
         && this.thrower != null
         && this.seal.feederUUID != null
         && this.digPos != null
         && this.seal.level().getFluidState(this.digPos.above()).is(FluidTags.WATER);
   }

   public void tick() {
      this.seal.setBasking(false);
      if (this.returnToPlayer) {
         this.seal.getNavigation().moveTo(this.thrower, 1.0);
         if (this.seal.distanceTo(this.thrower) < 2.0) {
            ItemStack stack = this.seal.getMainHandItem().copy();
            this.seal.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            ItemEntity item = AMCompat.spawnAtLocation(this.seal, stack);
            if (item != null) {
               double d0 = this.thrower.getX() - this.seal.getX();
               double d1 = this.thrower.getEyeY() - this.seal.getEyeY();
               double d2 = this.thrower.getZ() - this.seal.getZ();
               double lvt_7_1_ = Mth.sqrt((float)(d0 * d0 + d2 * d2));
               float pitch = (float)(-(Mth.atan2(d1, lvt_7_1_) * 57.2957763671875));
               float yaw = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
               float f8 = Mth.sin(pitch * 0.017453292F);
               float f2 = Mth.cos(pitch * 0.017453292F);
               float f3 = Mth.sin(yaw * 0.017453292F);
               float f4 = Mth.cos(yaw * 0.017453292F);
               float f5 = this.seal.getRandom().nextFloat() * 6.2831855F;
               float f6 = 0.02F * this.seal.getRandom().nextFloat();
               item.setDeltaMovement(
                  -f3 * f2 * 0.5F + Math.cos(f5) * f6,
                  -f8 * 0.2F + 0.1F + (this.seal.getRandom().nextFloat() - this.seal.getRandom().nextFloat()) * 0.1F,
                  f4 * f2 * 0.5F + Math.sin(f5) * f6
               );
            }

            this.seal.feederUUID = null;
            this.stop();
         }
      } else {
         double dist = this.seal.distanceToSqr(Vec3.atCenterOf(this.digPos.above()));
         double d0 = this.digPos.getX() + 0.5 - this.seal.getX();
         double d1 = this.digPos.getY() + 0.5 - this.seal.getEyeY();
         double d2 = this.digPos.getZ() + 0.5 - this.seal.getZ();
         float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
         if (dist < 2.0) {
            this.seal.getNavigation().stop();
            this.digTime++;
            if (this.digTime % 5 == 0) {
               SoundEvent sound = this.seal.level().getBlockState(this.digPos).getSoundType().getHitSound();
               this.seal.playSound(sound, 1.0F, 0.5F + this.seal.getRandom().nextFloat() * 0.5F);
            }

            if (this.digTime >= 100) {
               List<ItemStack> lootList = getItemStacks(this.seal);
               if (lootList.size() > 0) {
                  ItemStack copy = lootList.remove(0);
                  copy = copy.copy();
                  this.seal.setItemInHand(InteractionHand.MAIN_HAND, copy);

                  for (ItemStack stack : lootList) {
                     AMCompat.spawnAtLocation(this.seal, stack.copy());
                  }

                  this.returnToPlayer = true;
               }

               this.seal.setDigging(false);
               this.digTime = 0;
            } else {
               this.seal.setDigging(true);
            }
         } else {
            this.seal.setDigging(false);
            this.seal.getNavigation().moveTo(this.digPos.getX(), this.digPos.getY(), this.digPos.getZ(), 1.0);
            this.seal.setYRot(f);
         }
      }
   }

   public void stop() {
      this.seal.setDigging(false);
      this.digPos = null;
      this.thrower = null;
      this.digTime = 0;
      this.returnToPlayer = false;
      this.seal.fishFeedings = 0;
      if (!this.seal.getMainHandItem().isEmpty()) {
         AMCompat.spawnAtLocation(this.seal, this.seal.getMainHandItem().copy());
         this.seal.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
      }
   }

   private BlockPos genSeafloorPos(BlockPos parent) {
      LevelAccessor world = this.seal.level();
      RandomSource random = this.seal.getRandom();
      int range = 15;

      for (int i = 0; i < 15; i++) {
         BlockPos seafloor = parent.offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);

         while (world.getFluidState(seafloor).is(FluidTags.WATER) && seafloor.getY() > 1) {
            seafloor = seafloor.below();
         }

         BlockState state = world.getBlockState(seafloor);
         if (state.is(AMTagRegistry.SEAL_DIGABLES)) {
            return seafloor;
         }
      }

      return null;
   }

   private BlockPos genDigPos() {
      RandomSource random = this.seal.getRandom();
      int range = 15;
      if (this.seal.isInWater()) {
         return this.genSeafloorPos(this.seal.blockPosition());
      } else {
         for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.seal.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

            while (this.seal.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
               blockpos1 = blockpos1.below();
            }

            if (this.seal.level().getFluidState(blockpos1).is(FluidTags.WATER)) {
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
