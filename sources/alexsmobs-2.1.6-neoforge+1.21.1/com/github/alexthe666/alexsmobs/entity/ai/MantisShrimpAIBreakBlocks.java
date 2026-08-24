package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

public class MantisShrimpAIBreakBlocks extends Goal {
   private final EntityMantisShrimp mantisShrimp;
   private int idleAtFlowerTime = 0;
   private int timeoutCounter = 0;
   private int searchCooldown = 0;
   private boolean isAboveDestinationBear;
   private BlockPos destinationBlock;
   private final MantisShrimpAIBreakBlocks.BlockSorter targetSorter;

   public MantisShrimpAIBreakBlocks(EntityMantisShrimp mantisShrimp) {
      this.mantisShrimp = mantisShrimp;
      this.targetSorter = new MantisShrimpAIBreakBlocks.BlockSorter(mantisShrimp);
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public void start() {
      super.start();
   }

   public boolean canUse() {
      if (!this.mantisShrimp.isBaby()
         && (this.mantisShrimp.getTarget() == null || !this.mantisShrimp.getTarget().isAlive())
         && this.mantisShrimp.getCommand() == 3
         && !this.mantisShrimp.getMainHandItem().isEmpty()) {
         if (this.searchCooldown <= 0) {
            this.resetTarget();
            this.searchCooldown = 100 + this.mantisShrimp.getRandom().nextInt(200);
            return this.destinationBlock != null;
         }

         this.searchCooldown--;
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.destinationBlock != null
         && this.timeoutCounter < 1200
         && (this.mantisShrimp.getTarget() == null || !this.mantisShrimp.getTarget().isAlive())
         && this.mantisShrimp.getCommand() == 3
         && !this.mantisShrimp.getMainHandItem().isEmpty();
   }

   public void stop() {
      this.searchCooldown = 50;
      this.timeoutCounter = 0;
      this.destinationBlock = null;
   }

   public double getTargetDistanceSq() {
      return 2.3;
   }

   public void tick() {
      BlockPos blockpos = this.destinationBlock;
      float yDist = (float)Math.abs(blockpos.getY() - this.mantisShrimp.getY() - this.mantisShrimp.getBbHeight() / 2.0F);
      this.mantisShrimp.getNavigation().moveTo(blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5, 1.0);
      if (this.isWithinXZDist(blockpos, this.mantisShrimp.position(), this.getTargetDistanceSq()) && !(yDist > 2.0F)) {
         this.isAboveDestinationBear = true;
         this.timeoutCounter--;
      } else {
         this.isAboveDestinationBear = false;
         this.timeoutCounter++;
      }

      if (this.timeoutCounter > 2400) {
         this.stop();
      }

      if (this.getIsAboveDestination()) {
         this.mantisShrimp.lookAt(Anchor.EYES, new Vec3(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY(), this.destinationBlock.getZ() + 0.5));
         if (this.idleAtFlowerTime >= 2) {
            this.idleAtFlowerTime = 0;
            this.breakBlock();
            this.stop();
         } else {
            this.mantisShrimp.punch();
            this.idleAtFlowerTime++;
         }
      }
   }

   private void resetTarget() {
      List<BlockPos> allBlocks = new ArrayList<>();
      int radius = 16;

      for (BlockPos pos : (List)BlockPos.betweenClosedStream(
            this.mantisShrimp.blockPosition().offset(-radius, -radius, -radius), this.mantisShrimp.blockPosition().offset(radius, radius, radius)
         )
         .map(BlockPos::immutable)
         .collect(Collectors.toList())) {
         if (!this.mantisShrimp.level().isEmptyBlock(pos)
            && this.shouldMoveTo(this.mantisShrimp.level(), pos)
            && (!this.mantisShrimp.isInWater() || this.isBlockTouchingWater(pos))) {
            allBlocks.add(pos);
         }
      }

      if (!allBlocks.isEmpty()) {
         allBlocks.sort(this.targetSorter);

         for (BlockPos posx : allBlocks) {
            if (this.hasLineOfSightBlock(posx)) {
               this.destinationBlock = posx;
               return;
            }
         }
      }

      this.destinationBlock = null;
   }

   private boolean isBlockTouchingWater(BlockPos pos) {
      for (Direction dir : Direction.values()) {
         if (this.mantisShrimp.level().getFluidState(pos.relative(dir)).is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
   }

   protected boolean getIsAboveDestination() {
      return this.isAboveDestinationBear;
   }

   private void breakBlock() {
      if (this.shouldMoveTo(this.mantisShrimp.level(), this.destinationBlock)) {
         BlockState state = this.mantisShrimp.level().getBlockState(this.destinationBlock);
         if (!this.mantisShrimp.level().isEmptyBlock(this.destinationBlock)
            && CommonHooks.canEntityDestroy(this.mantisShrimp.level(), this.destinationBlock, this.mantisShrimp)
            && state.getDestroySpeed(this.mantisShrimp.level(), this.destinationBlock) >= 0.0F) {
            this.mantisShrimp.level().destroyBlock(this.destinationBlock, true);
         }
      }
   }

   private boolean hasLineOfSightBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.mantisShrimp.getX(), this.mantisShrimp.getEyeY(), this.mantisShrimp.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.mantisShrimp.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this.mantisShrimp));
      return result.getBlockPos().equals(destinationBlock);
   }

   protected boolean shouldMoveTo(LevelReader worldIn, BlockPos pos) {
      Item blockItem = worldIn.getBlockState(pos).getBlock().asItem();
      return this.mantisShrimp.getMainHandItem().getItem() == blockItem;
   }

   public record BlockSorter(Entity entity) implements Comparator<BlockPos> {
      public int compare(BlockPos pos1, BlockPos pos2) {
         double distance1 = this.getDistance(pos1);
         double distance2 = this.getDistance(pos2);
         return Double.compare(distance1, distance2);
      }

      private double getDistance(BlockPos pos) {
         double deltaX = this.entity.getX() - (pos.getX() + 0.5);
         double deltaY = this.entity.getY() + this.entity.getEyeHeight() - (pos.getY() + 0.5);
         double deltaZ = this.entity.getZ() - (pos.getZ() + 0.5);
         return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
      }
   }
}
