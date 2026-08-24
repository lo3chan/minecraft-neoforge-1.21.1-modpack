package com.aetherteam.aether.entity.monster.dungeon.boss.goal;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SetPathUpOrDownGoal extends Goal {
   private final Slider slider;

   public SetPathUpOrDownGoal(Slider slider) {
      this.slider = slider;
   }

   public boolean canUse() {
      if (this.slider.getMoveDelay() == 1 && this.slider.getTargetPoint() == null && this.slider.getRandom().nextInt(3) == 0) {
         Direction moveDir = this.slider.getMoveDirection();
         return moveDir == null || moveDir.getAxis() != Axis.Y;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return false;
   }

   public void start() {
      Vec3 targetPos = getTargetOrCurrentPosition(this.slider);
      if (targetPos != null) {
         Vec3 currentPos = this.slider.position();
         AABB currentPath = calculatePathBox(this.slider.getBoundingBox(), targetPos.x() - currentPos.x(), 0.0, targetPos.z() - currentPos.z());
         Direction direction = currentPos.y() > targetPos.y() ? Direction.DOWN : Direction.UP;
         currentPath = Slider.calculateAdjacentBox(currentPath, direction);
         currentPath = currentPath.expandTowards(0.0, targetPos.y() - currentPos.y(), 0.0);
         MutableBlockPos pos = new MutableBlockPos();

         for (int x = Mth.floor(currentPath.minX); x < currentPath.maxX; x++) {
            for (int z = Mth.floor(currentPath.minZ); z < currentPath.maxZ; z++) {
               BlockState state = this.slider.level().getBlockState(pos.set(x, targetPos.y(), z));
               if (state.is(AetherTags.Blocks.SLIDER_UNBREAKABLE)) {
                  return;
               }
            }
         }

         double y = direction == Direction.UP ? Math.max(targetPos.y(), currentPos.y() + 1.0) : targetPos.y();
         this.slider.setMoveDirection(direction);
         this.slider.setTargetPoint(new Vec3(currentPos.x(), y, currentPos.z()));
      }
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   @Nullable
   private static Vec3 getTargetOrCurrentPosition(Slider slider) {
      LivingEntity target = slider.getTarget();
      return target == null ? null : target.position();
   }

   private static AABB calculatePathBox(AABB box, double x, double y, double z) {
      return box.expandTowards(x, y, z);
   }
}
