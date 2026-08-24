package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ElephantAIForageLeaves extends MoveToBlockGoal {
   private final EntityElephant elephant;
   private int idleAtLeavesTime = 0;
   private boolean isAboveDestinationBear;
   private int moveCooldown = 0;

   public ElephantAIForageLeaves(EntityElephant elephant) {
      super(elephant, 0.7, 32, 5);
      this.elephant = elephant;
   }

   public boolean canUse() {
      return !this.elephant.isBaby()
         && this.elephant.getControllingPassenger() == null
         && this.elephant.getControllingVillager() == null
         && this.elephant.getMainHandItem().isEmpty()
         && !this.elephant.aiItemFlag
         && super.canUse();
   }

   public void stop() {
      this.idleAtLeavesTime = 0;
   }

   public void start() {
      super.start();
      this.moveCooldown = 30 + this.elephant.getRandom().nextInt(50);
   }

   public double acceptedDistance() {
      return 4.0;
   }

   public boolean shouldRecalculatePath() {
      return this.moveCooldown == 0;
   }

   public void tick() {
      if (this.moveCooldown > 0) {
         this.moveCooldown--;
      }

      BlockPos blockpos = this.getMoveToTarget();
      if (!this.isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
         this.isAboveDestinationBear = false;
         this.tryTicks++;
         if (this.shouldRecalculatePath()) {
            this.moveCooldown = 30 + this.elephant.getRandom().nextInt(50);
            this.mob.getNavigation().moveTo(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5, this.speedModifier);
         }
      } else {
         this.isAboveDestinationBear = true;
         this.tryTicks = 0;
      }

      if (this.isReachedTarget() && Math.abs(this.elephant.getY() - this.blockPos.getY()) <= 3.0) {
         this.elephant.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
         if (this.elephant.getY() + 2.0 < this.blockPos.getY()) {
            if (this.elephant.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               this.elephant.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
            }

            this.elephant.setStanding(true);
            this.elephant.maxStandTime = 15;
         } else {
            this.elephant.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
            this.elephant.setStanding(false);
         }

         if (this.idleAtLeavesTime >= 10) {
            this.breakLeaves();
         } else {
            this.idleAtLeavesTime++;
         }
      }
   }

   protected void moveMobToBlock() {
   }

   protected int nextStartTick(PathfinderMob p_203109_1_) {
      return 100 + p_203109_1_.getRandom().nextInt(200);
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(new BlockPos((int)positionVec.x(), blockpos.getY(), (int)positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationBear;
   }

   private void breakLeaves() {
      if (AMPlatform.mobGriefing(this.elephant.level(), this.elephant)) {
         BlockState blockstate = this.elephant.level().getBlockState(this.blockPos);
         if (blockstate.is(AMTagRegistry.ELEPHANT_FOODBLOCKS)) {
            this.elephant.level().destroyBlock(this.blockPos, false);
            RandomSource rand = this.elephant.getRandom();
            ItemStack stack = new ItemStack(blockstate.getBlock().asItem());
            ItemEntity itementity = new ItemEntity(
               this.elephant.level(),
               this.blockPos.getX() + rand.nextFloat(),
               this.blockPos.getY() + rand.nextFloat(),
               this.blockPos.getZ() + rand.nextFloat(),
               stack
            );
            itementity.setDefaultPickUpDelay();
            this.elephant.level().addFreshEntity(itementity);
            if (blockstate.is(AMTagRegistry.DROPS_ACACIA_BLOSSOMS) && rand.nextInt(30) == 0) {
               ItemStack banana = new ItemStack((ItemLike)AMItemRegistry.ACACIA_BLOSSOM.get());
               ItemEntity itementity2 = new ItemEntity(
                  this.elephant.level(),
                  this.blockPos.getX() + rand.nextFloat(),
                  this.blockPos.getY() + rand.nextFloat(),
                  this.blockPos.getZ() + rand.nextFloat(),
                  banana
               );
               itementity2.setDefaultPickUpDelay();
               this.elephant.level().addFreshEntity(itementity2);
            }

            this.stop();
         }
      }
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return !this.elephant.aiItemFlag && worldIn.getBlockState(pos).is(AMTagRegistry.ELEPHANT_FOODBLOCKS) && this.canSeeBlock(pos);
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.elephant.getX(), this.elephant.getEyeY(), this.elephant.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.elephant.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this.elephant));
      return result.getBlockPos().equals(destinationBlock);
   }
}
