package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class HummingbirdAIWander extends Goal {
   private final EntityHummingbird fly;
   private final int rangeXZ;
   private final int rangeY;
   private final int chance;
   private final float speed;
   private Vec3 moveToPoint = null;

   public HummingbirdAIWander(EntityHummingbird fly, int rangeXZ, int rangeY, int chance, float speed) {
      this.setFlags(EnumSet.of(Flag.MOVE));
      this.fly = fly;
      this.rangeXZ = rangeXZ;
      this.rangeY = rangeY;
      this.chance = chance;
      this.speed = speed;
   }

   public boolean canUse() {
      return this.fly.hummingStill > 10 && this.fly.getRandom().nextInt(this.chance) == 0 && !this.fly.getMoveControl().hasWanted();
   }

   public void stop() {
      this.moveToPoint = null;
   }

   public boolean canContinueToUse() {
      return this.moveToPoint != null && this.fly.distanceToSqr(this.moveToPoint) > 0.85;
   }

   public void start() {
      this.moveToPoint = this.getRandomLocation();
      if (this.moveToPoint != null) {
         this.fly.getMoveControl().setWantedPosition(this.moveToPoint.x, this.moveToPoint.y, this.moveToPoint.z, this.speed);
      }
   }

   public void tick() {
      if (this.moveToPoint != null) {
         this.fly.getMoveControl().setWantedPosition(this.moveToPoint.x, this.moveToPoint.y, this.moveToPoint.z, this.speed);
      }
   }

   @Nullable
   private Vec3 getRandomLocation() {
      RandomSource random = this.fly.getRandom();
      BlockPos blockpos = null;
      BlockPos origin = this.fly.getFeederPos() == null ? this.fly.blockPosition() : this.fly.getFeederPos();

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = origin.offset(random.nextInt(this.rangeXZ) - this.rangeXZ / 2, 1, random.nextInt(this.rangeXZ) - this.rangeXZ / 2);

         while (this.fly.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 0) {
            blockpos1 = blockpos1.below();
         }

         blockpos1 = blockpos1.above(1 + random.nextInt(3));
         if (this.fly.level().isEmptyBlock(blockpos1.below())
            && this.fly.canBlockBeSeen(blockpos1)
            && this.fly.level().isEmptyBlock(blockpos1)
            && !this.fly.level().isEmptyBlock(blockpos1.below(2))) {
            blockpos = blockpos1;
         }
      }

      return blockpos == null ? null : new Vec3(blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5);
   }

   public boolean canBlockPosBeSeen(BlockPos pos) {
      double x = pos.getX() + 0.5F;
      double y = pos.getY() + 0.5F;
      double z = pos.getZ() + 0.5F;
      HitResult result = this.fly
         .level()
         .clip(
            new ClipContext(
               new Vec3(this.fly.getX(), this.fly.getY() + this.fly.getEyeHeight(), this.fly.getZ()), new Vec3(x, y, z), Block.COLLIDER, Fluid.NONE, this.fly
            )
         );
      double dist = result.getLocation().distanceToSqr(x, y, z);
      return dist <= 1.0 || result.getType() == Type.MISS;
   }
}
