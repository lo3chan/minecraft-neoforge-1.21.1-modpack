package net.astralya.hexalia.gameplay.moths.ai;

import java.util.EnumSet;
import java.util.List;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HoverAroundLightGoal extends Goal {
   private static final int SCAN_COOLDOWN_MIN = 40;
   private static final int SCAN_COOLDOWN_MAX = 80;
   private static final int HOVER_REPATH_INTERVAL = 20;
   private static final int LIGHT_SEARCH_XZ = 10;
   private static final int LIGHT_SEARCH_Y = 5;
   private static final double ORBIT_RADIUS_MIN = 1.5;
   private static final double ORBIT_RADIUS_MAX = 3.0;
   private static final double HOVER_Y_MIN = 0.7;
   private static final double HOVER_Y_MAX = 1.4;
   private static final int ORBIT_DURATION_MIN_TICKS = 120;
   private static final int ORBIT_DURATION_MAX_TICKS = 280;
   private static final double ARRIVE_DIST_SQR = 1.44;
   private final Mob mob;
   private final double speed;
   private BlockPos lightPos;
   private Vec3 hoverTarget;
   private int scanCooldown;
   private int orbitTicksRemaining;
   private int repathCooldown;

   public HoverAroundLightGoal(Mob mob, double speed) {
      this.mob = mob;
      this.speed = speed;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.mob.level().isClientSide) {
         return false;
      } else if (this.mob instanceof SilkMothEntity moth && moth.isEggReady()) {
         return false;
      } else if (this.scanCooldown > 0) {
         this.scanCooldown--;
         return false;
      } else {
         this.scanCooldown = 40 + this.mob.getRandom().nextInt(41);
         return this.findClosestLight();
      }
   }

   public boolean canContinueToUse() {
      if (this.mob.level().isClientSide) {
         return false;
      } else if (this.mob instanceof SilkMothEntity moth && moth.isEggReady()) {
         return false;
      } else if (this.lightPos == null) {
         return false;
      } else if (this.orbitTicksRemaining <= 0) {
         return false;
      } else {
         BlockState state = this.mob.level().getBlockState(this.lightPos);
         return state.is(ModTags.Blocks.ATTRACTS_MOTH);
      }
   }

   public void start() {
      this.orbitTicksRemaining = 120 + this.mob.getRandom().nextInt(161);
      this.repathCooldown = 0;
      this.pickAndMoveToNextHoverTarget();
   }

   public void stop() {
      this.lightPos = null;
      this.hoverTarget = null;
      this.orbitTicksRemaining = 0;
      this.repathCooldown = 0;
   }

   public void tick() {
      this.orbitTicksRemaining--;
      if (this.lightPos != null) {
         if (this.repathCooldown > 0) {
            this.repathCooldown--;
         }

         this.mob.getLookControl().setLookAt(this.lightPos.getX() + 0.5, this.lightPos.getY() + 0.5, this.lightPos.getZ() + 0.5);
         if (this.hoverTarget == null) {
            this.pickAndMoveToNextHoverTarget();
         } else {
            double distSqr = this.mob.position().distanceToSqr(this.hoverTarget);
            if (distSqr <= 1.44) {
               this.pickAndMoveToNextHoverTarget();
            } else {
               if (this.mob.getNavigation().isDone() && this.repathCooldown <= 0) {
                  this.pickAndMoveToNextHoverTarget();
               }
            }
         }
      }
   }

   private void pickAndMoveToNextHoverTarget() {
      Level level = this.mob.level();
      Vec3 target = this.pickHoverTarget(level, this.lightPos);
      if (target == null) {
         this.lightPos = null;
      } else {
         target = this.applySeparationBias(level, target);
         this.hoverTarget = target;
         this.repathCooldown = 20;
         this.mob.getNavigation().moveTo(this.hoverTarget.x, this.hoverTarget.y, this.hoverTarget.z, this.speed);
      }
   }

   private boolean findClosestLight() {
      Level level = this.mob.level();
      BlockPos origin = this.mob.blockPosition();
      MutableBlockPos cursor = new MutableBlockPos();
      BlockPos best = null;
      int bestDistSqr = 2147483647;
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();

      for (int dx = -10; dx <= 10; dx++) {
         for (int dy = -5; dy <= 5; dy++) {
            for (int dz = -10; dz <= 10; dz++) {
               cursor.set(ox + dx, oy + dy, oz + dz);
               BlockState state = level.getBlockState(cursor);
               if (state.is(ModTags.Blocks.ATTRACTS_MOTH)) {
                  int distSqr = dx * dx + dy * dy + dz * dz;
                  if (distSqr < bestDistSqr) {
                     bestDistSqr = distSqr;
                     best = cursor.immutable();
                  }
               }
            }
         }
      }

      if (best == null) {
         return false;
      } else {
         this.lightPos = best;
         return true;
      }
   }

   private Vec3 pickHoverTarget(Level level, BlockPos lightPos) {
      Vec3 center = Vec3.atCenterOf(lightPos);

      for (int attempt = 0; attempt < 10; attempt++) {
         float angle = this.mob.getRandom().nextFloat() * 6.2831855F;
         double radius = 1.5 + this.mob.getRandom().nextDouble() * 1.5;
         double y = 0.7 + this.mob.getRandom().nextDouble() * 0.7;
         double x = center.x + Mth.cos(angle) * radius;
         double z = center.z + Mth.sin(angle) * radius;
         Vec3 candidate = new Vec3(x, center.y + y, z);
         BlockPos pos = BlockPos.containing(candidate);
         if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir()) {
            return candidate;
         }
      }

      return null;
   }

   private Vec3 applySeparationBias(Level level, Vec3 target) {
      AABB box = this.mob.getBoundingBox().inflate(2.5);
      List<Mob> nearby = level.getEntitiesOfClass(Mob.class, box, e -> e != this.mob && e.getType() == this.mob.getType());
      if (nearby.isEmpty()) {
         return target;
      } else {
         Vec3 away = Vec3.ZERO;

         for (Mob other : nearby) {
            double distSqr = this.mob.position().distanceToSqr(other.position());
            if (!(distSqr <= 1.0E-4) && !(distSqr > 2.5600000000000005)) {
               Vec3 delta = this.mob.position().subtract(other.position());
               Vec3 norm = delta.normalize();
               away = away.add(norm);
            }
         }

         if (away.lengthSqr() <= 1.0E-4) {
            return target;
         } else {
            Vec3 bias = away.normalize().scale(0.6);
            return new Vec3(target.x + bias.x, target.y, target.z + bias.z);
         }
      }
   }
}
