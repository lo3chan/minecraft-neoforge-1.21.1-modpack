package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LeafcutterAntAIForageLeaves extends MoveToBlockGoal {
   private final EntityLeafcutterAnt ant;
   private int idleAtLeavesTime = 0;
   private int randomLeafCheckCooldown = 40;
   private BlockPos logStartPos = null;
   private BlockPos logTopPos = null;
   private final int searchRange;
   private final int verticalSearchRange;
   private int moveToCooldown = 0;

   public LeafcutterAntAIForageLeaves(EntityLeafcutterAnt LeafcutterAnt) {
      super(LeafcutterAnt, 1.0, 15, 3);
      this.searchRange = 15;
      this.verticalSearchRange = 3;
      this.ant = LeafcutterAnt;
   }

   public boolean canUse() {
      return !this.ant.isBaby() && !this.ant.hasLeaf() && !this.ant.isBaby() && !this.ant.isQueen() && super.canUse();
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && !this.ant.hasLeaf();
   }

   public void stop() {
      this.idleAtLeavesTime = 0;
      this.logStartPos = null;
      this.logTopPos = null;
   }

   public void start() {
      this.moveToCooldown = 10 + this.ant.getRandom().nextInt(10);
   }

   public double acceptedDistance() {
      return 2.0;
   }

   public boolean shouldRecalculatePath() {
      return this.tryTicks % 40 == 0 && this.logStartPos == null;
   }

   public void tick() {
      if (this.moveToCooldown > 0) {
         this.moveToCooldown--;
      }

      if (this.randomLeafCheckCooldown > 0) {
         this.randomLeafCheckCooldown--;
      } else {
         this.randomLeafCheckCooldown = 30 + this.ant.getRandom().nextInt(50);

         for (Direction dir : Direction.values()) {
            BlockPos offset = this.ant.blockPosition().relative(dir);
            if (this.isValidTarget(this.ant.level(), offset) && this.ant.getRandom().nextInt(1) == 0) {
               this.blockPos = offset;
               this.logStartPos = null;
            }
         }
      }

      if (this.ant.getAttachmentFacing() == Direction.UP) {
         this.ant.getMoveControl().setWantedPosition(this.blockPos.getX() + 0.5F, this.blockPos.getY() - 1.0, this.blockPos.getZ() + 0.5F, 1.0);
         this.ant.setDeltaMovement(this.ant.getDeltaMovement().add(0.0, 0.5, 0.0));
         if (this.ant.getRandom().nextInt(2) == 0 && this.isValidTarget(this.ant.level(), this.ant.blockPosition().above())) {
            this.blockPos = this.ant.blockPosition().above();
         }
      } else if (!(this.blockPos.getY() > this.ant.getY() + 2.0) && this.logStartPos == null) {
         super.tick();
         this.logStartPos = null;
      } else {
         this.ant.getNavigation().stop();
         if (this.ant.getRandom().nextInt(5) == 0 && this.isValidTarget(this.ant.level(), this.ant.blockPosition().below())) {
            this.blockPos = this.ant.blockPosition().below();
         }

         if (this.logStartPos == null) {
            for (int i = 0; i < 15; i++) {
               BlockPos test = this.blockPos
                  .offset(6 - this.ant.getRandom().nextInt(12), -this.ant.getRandom().nextInt(7), 6 - this.ant.getRandom().nextInt(12));
               if (this.ant.level().getBlockState(test).is(BlockTags.LOGS)) {
                  this.logStartPos = test;
                  break;
               }
            }
         } else {
            double xDif = this.logStartPos.getX() + 0.5 - this.ant.getX();
            double zDif = this.logStartPos.getZ() + 0.5 - this.ant.getZ();
            float f = (float)(Mth.atan2(zDif, xDif) * 57.2957763671875) - 90.0F;
            this.ant.setYRot(f);
            this.ant.yBodyRot = this.ant.getYRot();
            Vec3 vec = new Vec3(this.logStartPos.getX() + 0.5, this.ant.getY(), this.logStartPos.getZ() + 0.5);
            vec = vec.subtract(this.ant.position());
            if (this.ant.onGround() || this.ant.onClimbable()) {
               this.ant.setDeltaMovement(vec.normalize().multiply(0.1, 0.0, 0.1).add(0.0, this.ant.getDeltaMovement().y, 0.0));
            }

            if (this.moveToCooldown <= 0) {
               this.moveToCooldown = 20 + this.ant.getRandom().nextInt(30);
               this.ant.getNavigation().moveTo(this.logStartPos.getX(), this.ant.getY(), this.logStartPos.getZ(), 1.0);
            }

            if (Math.abs(xDif) < 0.6 && Math.abs(zDif) < 0.6) {
               this.ant.setDeltaMovement(this.ant.getDeltaMovement().multiply(0.0, 1.0, 0.0));
               this.ant.getMoveControl().setWantedPosition(this.logStartPos.getX() + 0.5, this.ant.getY() + 2.0, this.logStartPos.getZ() + 0.5, 1.0);
               BlockPos test = new BlockPos(this.logStartPos.getX(), (int)this.ant.getY(), this.logStartPos.getZ());
               if (!this.ant.level().getBlockState(test).is(BlockTags.LOGS) && this.ant.getAttachmentFacing() == Direction.DOWN) {
                  this.stop();
                  return;
               }
            }
         }

         this.tryTicks++;
      }

      if (this.isReachedTarget() || this.ant.blockPosition().above().equals(this.blockPos)) {
         this.ant.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
         this.ant.setAnimation(EntityLeafcutterAnt.ANIMATION_BITE);
         if (this.idleAtLeavesTime >= 6) {
            this.ant.setLeafHarvestedPos(this.blockPos);
            this.ant.setLeafHarvestedState(this.ant.level().getBlockState(this.blockPos));
            if (!this.ant.hasLeaf()) {
               this.breakLeaves();
            }

            this.ant.setLeaf(true);
            this.stop();
            this.idleAtLeavesTime = 0;
         } else {
            this.idleAtLeavesTime++;
         }
      }
   }

   private void breakLeaves() {
      BlockState blockstate = this.ant.level().getBlockState(this.blockPos);
      if (blockstate.is(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES) && AMPlatform.mobGriefing(this.ant.level(), this.ant)) {
         this.ant.level().destroyBlock(this.blockPos, false);
         if (this.ant.getRandom().nextFloat() > AMConfig.leafcutterAntBreakLeavesChance) {
            this.ant.level().setBlockAndUpdate(this.blockPos, blockstate);
         }
      }
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).is(AMTagRegistry.LEAFCUTTER_ANT_BREAKABLES);
   }

   protected boolean findNearestBlock() {
      int i = this.searchRange;
      int j = this.verticalSearchRange;
      BlockPos blockpos = this.mob.blockPosition();
      if (this.ant.hasHive() && this.ant.getHivePos() != null) {
         blockpos = this.ant.getHivePos();
         i *= 2;
      }

      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for (int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
         for (int l = 0; l < i; l++) {
            for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
               for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                  blockpos$mutableblockpos.setWithOffset(blockpos, i1, k - 1, j1);
                  if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidTarget(this.mob.level(), blockpos$mutableblockpos)) {
                     this.blockPos = blockpos$mutableblockpos;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }
}
