package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovementControllerCustomCollisions extends MoveControl {
   public MovementControllerCustomCollisions(Mob mob) {
      super(mob);
   }

   public void tick() {
      if (this.operation == Operation.STRAFE) {
         float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
         float f1 = (float)this.speedModifier * f;
         float f2 = this.strafeForwards;
         float f3 = this.strafeRight;
         float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 *= f4;
         f3 *= f4;
         float f5 = Mth.sin(this.mob.getYRot() * 0.017453292F);
         float f6 = Mth.cos(this.mob.getYRot() * 0.017453292F);
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         if (!this.isWalkable(f7, f8)) {
            this.strafeForwards = 1.0F;
            this.strafeRight = 0.0F;
         }

         this.mob.setSpeed(f1);
         this.mob.setZza(this.strafeForwards);
         this.mob.setXxa(this.strafeRight);
         this.operation = Operation.WAIT;
      } else if (this.operation == Operation.MOVE_TO) {
         this.operation = Operation.WAIT;
         double d0 = this.wantedX - this.mob.getX();
         double d1 = this.wantedZ - this.mob.getZ();
         double d2 = this.wantedY - this.mob.getY();
         double d3 = d0 * d0 + d2 * d2 + d1 * d1;
         if (d3 < 2.500000277905201E-7) {
            this.mob.setZza(0.0F);
            return;
         }

         float f9 = (float)(Mth.atan2(d1, d0) * 57.2957763671875) - 90.0F;
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         BlockPos blockpos = this.mob.blockPosition();
         BlockState blockstate = this.mob.level().getBlockState(blockpos);
         VoxelShape voxelshape = blockstate.getBlockSupportShape(this.mob.level(), blockpos);
         if ((!(this.mob instanceof ICustomCollisions) || !((ICustomCollisions)this.mob).canPassThrough(blockpos, blockstate, voxelshape))
            && (
               d2 > this.mob.maxUpStep() && d0 * d0 + d1 * d1 < Math.max(1.0F, this.mob.getBbWidth())
                  || !voxelshape.isEmpty()
                     && this.mob.getY() < voxelshape.max(Axis.Y) + blockpos.getY()
                     && !blockstate.is(BlockTags.DOORS)
                     && !blockstate.is(BlockTags.FENCES)
            )) {
            this.mob.getJumpControl().jump();
            this.operation = Operation.JUMPING;
         }
      } else if (this.operation == Operation.JUMPING) {
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         if (this.mob.onGround()) {
            this.operation = Operation.WAIT;
         }
      } else {
         this.mob.setZza(0.0F);
      }
   }

   private boolean isWalkable(float p_234024_1_, float p_234024_2_) {
      PathNavigation pathnavigator = this.mob.getNavigation();
      if (pathnavigator != null) {
         NodeEvaluator nodeprocessor = pathnavigator.getNodeEvaluator();
         if (nodeprocessor != null
            && nodeprocessor.getPathType(this.mob, BlockPos.containing(this.mob.getX() + p_234024_1_, this.mob.getY(), this.mob.getZ() + p_234024_2_))
               != PathType.WALKABLE) {
            return false;
         }
      }

      return true;
   }
}
