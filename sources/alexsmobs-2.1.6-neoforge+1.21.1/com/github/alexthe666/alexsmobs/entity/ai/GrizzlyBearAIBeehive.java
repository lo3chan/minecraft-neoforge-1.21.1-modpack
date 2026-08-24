package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GrizzlyBearAIBeehive extends MoveToBlockGoal {
   private final EntityGrizzlyBear bear;
   private int idleAtHiveTime = 0;
   private boolean isAboveDestinationBear;

   public GrizzlyBearAIBeehive(EntityGrizzlyBear bear) {
      super(bear, 1.0, 32, 8);
      this.bear = bear;
   }

   public boolean canUse() {
      return !this.bear.isBaby() && super.canUse();
   }

   public void stop() {
      this.idleAtHiveTime = 0;
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

      if (this.isReachedTarget() && Math.abs(this.bear.getY() - this.blockPos.getY()) <= 3.0) {
         this.bear.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
         if (this.bear.getY() + 2.0 < this.blockPos.getY()) {
            this.bear.setAnimation(EntityGrizzlyBear.ANIMATION_MAUL);
            this.bear.maxStandTime = 60;
            this.bear.setStanding(true);
         } else if (this.bear.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.bear.setAnimation(this.bear.getRandom().nextBoolean() ? EntityGrizzlyBear.ANIMATION_SWIPE_L : EntityGrizzlyBear.ANIMATION_SWIPE_R);
         }

         if (this.idleAtHiveTime >= 20) {
            this.eatHive();
         } else {
            this.idleAtHiveTime++;
         }
      }
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationBear;
   }

   private void eatHive() {
      if (AMPlatform.mobGriefing(this.bear.level(), this.bear)) {
         BlockState blockstate = this.bear.level().getBlockState(this.blockPos);
         if (blockstate.is(AMTagRegistry.GRIZZLY_BEEHIVE) && this.bear.level().getBlockEntity(this.blockPos) instanceof BeehiveBlockEntity) {
            RandomSource rand = this.bear.getRandom();
            BeehiveBlockEntity beehivetileentity = (BeehiveBlockEntity)this.bear.level().getBlockEntity(this.blockPos);
            beehivetileentity.emptyAllLivingFromHive(null, blockstate, BeeReleaseStatus.EMERGENCY);
            this.bear.level().updateNeighbourForOutputSignal(this.blockPos, blockstate.getBlock());
            ItemStack stack = new ItemStack(Items.HONEYCOMB);
            int level = 0;
            if (blockstate.getBlock() instanceof BeehiveBlock) {
               level = (Integer)blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
            }

            for (int i = 0; i < level; i++) {
               ItemEntity itementity = new ItemEntity(
                  this.bear.level(),
                  this.blockPos.getX() + rand.nextFloat(),
                  this.blockPos.getY() + rand.nextFloat(),
                  this.blockPos.getZ() + rand.nextFloat(),
                  stack
               );
               itementity.setDefaultPickUpDelay();
               this.bear.level().addFreshEntity(itementity);
            }

            this.bear.level().destroyBlock(this.blockPos, false);
            if (blockstate.getBlock() instanceof BeehiveBlock) {
               this.bear.level().setBlockAndUpdate(this.blockPos, (BlockState)blockstate.setValue(BeehiveBlock.HONEY_LEVEL, 0));
            }

            double d0 = 15.0;

            for (Bee bee : this.bear
               .level()
               .getEntitiesOfClass(
                  Bee.class,
                  new AABB(
                     this.blockPos.getX() - d0,
                     this.blockPos.getY() - d0,
                     this.blockPos.getZ() - d0,
                     this.blockPos.getX() + d0,
                     this.blockPos.getY() + d0,
                     this.blockPos.getZ() + d0
                  )
               )) {
               bee.setRemainingPersistentAngerTime(100);
               bee.setTarget(this.bear);
               bee.setStayOutOfHiveCountdown(400);
            }

            this.stop();
         }
      }
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).is(AMTagRegistry.GRIZZLY_BEEHIVE)
         && worldIn.getBlockEntity(pos) instanceof BeehiveBlockEntity
         && worldIn.getBlockState(pos).getBlock() instanceof BeehiveBlock) {
         int i = (Integer)worldIn.getBlockState(pos).getValue(BeehiveBlock.HONEY_LEVEL);
         return i > 0;
      } else {
         return false;
      }
   }
}
