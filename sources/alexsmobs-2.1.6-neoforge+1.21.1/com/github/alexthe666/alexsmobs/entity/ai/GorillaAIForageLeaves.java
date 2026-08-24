package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GorillaAIForageLeaves extends MoveToBlockGoal {
   private final EntityGorilla gorilla;
   private int idleAtLeavesTime = 0;
   private boolean isAboveDestinationBear;

   public GorillaAIForageLeaves(EntityGorilla gorilla) {
      super(gorilla, 1.0, 32, 3);
      this.gorilla = gorilla;
   }

   public boolean canUse() {
      return !this.gorilla.isBaby() && this.gorilla.getMainHandItem().isEmpty() && super.canUse();
   }

   public void stop() {
      this.idleAtLeavesTime = 0;
   }

   public double acceptedDistance() {
      return 2.0;
   }

   public void tick() {
      super.tick();
      BlockPos blockpos = this.getMoveToTarget();
      if (!this.isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
         this.isAboveDestinationBear = false;
         this.tryTicks++;
         if (this.shouldRecalculatePath()) {
            this.mob.getNavigation().moveTo(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5, this.speedModifier);
         }
      } else {
         this.isAboveDestinationBear = true;
         this.tryTicks--;
      }

      if (this.isReachedTarget() && Math.abs(this.gorilla.getY() - this.blockPos.getY()) <= 3.0) {
         this.gorilla.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
         if (this.gorilla.getY() + 2.0 < this.blockPos.getY()) {
            this.gorilla.setAnimation(this.gorilla.getRandom().nextBoolean() ? EntityGorilla.ANIMATION_BREAKBLOCK_L : EntityGorilla.ANIMATION_BREAKBLOCK_R);
            this.gorilla.maxStandTime = 60;
            this.gorilla.setStanding(true);
         } else if (this.gorilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.gorilla.setAnimation(this.gorilla.getRandom().nextBoolean() ? EntityGorilla.ANIMATION_BREAKBLOCK_L : EntityGorilla.ANIMATION_BREAKBLOCK_R);
         }

         if (this.idleAtLeavesTime >= 20) {
            this.breakLeaves();
         } else {
            this.idleAtLeavesTime++;
         }
      }
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationBear;
   }

   private void breakLeaves() {
      if (AMPlatform.mobGriefing(this.gorilla.level(), this.gorilla)) {
         BlockState blockstate = this.gorilla.level().getBlockState(this.blockPos);
         if (blockstate.is(AMTagRegistry.GORILLA_BREAKABLES)) {
            this.gorilla.level().destroyBlock(this.blockPos, false);
            RandomSource rand = this.gorilla.getRandom();
            ItemStack stack = new ItemStack(blockstate.getBlock().asItem());
            ItemEntity itementity = new ItemEntity(
               this.gorilla.level(),
               this.blockPos.getX() + rand.nextFloat(),
               this.blockPos.getY() + rand.nextFloat(),
               this.blockPos.getZ() + rand.nextFloat(),
               stack
            );
            itementity.setDefaultPickUpDelay();
            this.gorilla.level().addFreshEntity(itementity);
            if (blockstate.is(AMTagRegistry.DROPS_BANANAS) && rand.nextInt(30) == 0) {
               ItemStack banana = new ItemStack((ItemLike)AMItemRegistry.BANANA.get());
               ItemEntity itementity2 = new ItemEntity(
                  this.gorilla.level(),
                  this.blockPos.getX() + rand.nextFloat(),
                  this.blockPos.getY() + rand.nextFloat(),
                  this.blockPos.getZ() + rand.nextFloat(),
                  banana
               );
               itementity2.setDefaultPickUpDelay();
               this.gorilla.level().addFreshEntity(itementity2);
            }

            this.stop();
         }
      }
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).is(AMTagRegistry.GORILLA_BREAKABLES);
   }
}
