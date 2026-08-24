package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HummingbirdAIPollinate extends MoveToBlockGoal {
   private final EntityHummingbird bird;
   private int idleAtFlowerTime = 0;
   private boolean isAboveDestinationBear;

   public HummingbirdAIPollinate(EntityHummingbird bird) {
      super(bird, 1.0, 32, 8);
      this.bird = bird;
   }

   public boolean canUse() {
      return !this.bird.isBaby() && this.bird.pollinateCooldown == 0 && super.canUse();
   }

   public void stop() {
      this.idleAtFlowerTime = 0;
   }

   public double acceptedDistance() {
      return 3.0;
   }

   public void tick() {
      super.tick();
      BlockPos blockpos = this.getMoveToTarget();
      if (!this.isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
         this.isAboveDestinationBear = false;
         this.tryTicks++;
         double speedLoc = this.speedModifier;
         if (this.mob.distanceToSqr(blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5) >= 3.0) {
            speedLoc = this.speedModifier * 0.3;
         }

         this.mob.getMoveControl().setWantedPosition(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5, speedLoc);
      } else {
         this.isAboveDestinationBear = true;
         this.tryTicks--;
      }

      if (this.isReachedTarget() && Math.abs(this.bird.getY() - this.blockPos.getY()) <= 2.0) {
         this.bird.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
         if (this.idleAtFlowerTime >= 20) {
            this.pollinate();
            this.stop();
         } else {
            this.idleAtFlowerTime++;
         }
      }
   }

   private boolean isGrowable(BlockPos pos, ServerLevel world) {
      BlockState blockstate = world.getBlockState(pos);
      Block block = blockstate.getBlock();
      return block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate);
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationBear;
   }

   private void pollinate() {
      this.bird.level().levelEvent(2005, this.blockPos, 0);
      this.bird.setCropsPollinated(this.bird.getCropsPollinated() + 1);
      this.bird.pollinateCooldown = 200;
      if (this.bird.getCropsPollinated() > 3) {
         if (this.isGrowable(this.blockPos, (ServerLevel)this.bird.level())) {
            BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), this.bird.level(), this.blockPos);
         }

         this.bird.setCropsPollinated(0);
      }
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return !worldIn.getBlockState(pos).is(AMTagRegistry.HUMMINGBIRD_POLLINATES) ? false : this.bird.pollinateCooldown == 0 && this.bird.canBlockBeSeen(pos);
   }
}
