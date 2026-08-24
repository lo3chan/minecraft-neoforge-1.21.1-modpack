package net.astralya.hexalia.gameplay.cacofey.ai;

import java.util.EnumSet;
import java.util.List;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CacofeyStealGoal extends Goal {
   private static final double SCAN_RADIUS = 12.0;
   private static final double STEAL_RADIUS = 1.8;
   private static final int SCAN_INTERVAL = 40;
   private static final int FLEE_TICKS = 80;
   private static final int STEAL_COOLDOWN = 1200;
   private static final float APPROACH_SPEED = 1.0F;
   private static final float FLEE_SPEED = 2.4F;
   private final CacofeyEntity cacofey;
   private CacofeyStealGoal.Phase phase = CacofeyStealGoal.Phase.SCAN;
   private Player target = null;
   private int phaseTimer = 0;
   private int scanTimer = 0;
   private double fleeOriginY = 0.0;

   public CacofeyStealGoal(CacofeyEntity cacofey) {
      this.cacofey = cacofey;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.cacofey.isTame()) {
         return false;
      } else if (this.cacofey.stealCooldown > 0) {
         return false;
      } else if (this.phase != CacofeyStealGoal.Phase.SCAN) {
         return true;
      } else if (this.scanTimer++ < 40) {
         return false;
      } else {
         this.scanTimer = 0;
         if (this.findTarget()) {
            this.phase = CacofeyStealGoal.Phase.APPROACH;
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean canContinueToUse() {
      if (this.cacofey.isTame()) {
         return false;
      } else {
         return this.phase != CacofeyStealGoal.Phase.FLEE && this.phase != CacofeyStealGoal.Phase.CONSUME
            ? this.target != null && this.target.isAlive() && !this.target.isCreative()
            : true;
      }
   }

   public void start() {
      this.phaseTimer = 0;
      this.cacofey.setInspecting(true);
      this.cacofey
         .level()
         .playSound(
            null,
            this.cacofey.getX(),
            this.cacofey.getY(),
            this.cacofey.getZ(),
            (SoundEvent)ModSoundEvents.CACOFEY_GIGGLE.get(),
            SoundSource.NEUTRAL,
            0.6F,
            1.1F + this.cacofey.getRandom().nextFloat() * 0.2F
         );
   }

   public void stop() {
      this.cacofey.setInspecting(false);
      this.phase = CacofeyStealGoal.Phase.SCAN;
      this.target = null;
      this.phaseTimer = 0;
      this.scanTimer = 0;
   }

   public void tick() {
      switch (this.phase) {
         case APPROACH:
            this.tickApproach();
            break;
         case STEAL:
            this.tickSteal();
            break;
         case FLEE:
            this.tickFlee();
            break;
         case CONSUME:
            this.tickConsume();
      }
   }

   private void tickApproach() {
      if (this.target != null && this.target.isAlive()) {
         this.cacofey.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
         this.cacofey.getNavigation().moveTo(this.target, 1.0);
         if (this.cacofey.distanceTo(this.target) <= 1.8) {
            this.cacofey.setInspecting(false);
            this.cacofey.getNavigation().stop();
            this.phase = CacofeyStealGoal.Phase.STEAL;
            this.phaseTimer = 0;
         }
      } else {
         this.reset();
      }
   }

   private void tickSteal() {
      if (this.target != null && this.target.isAlive()) {
         ItemStack stolen = findEdibleItem(this.target);
         if (stolen.isEmpty()) {
            this.reset();
         } else {
            ItemStack display = stolen.copyWithCount(1);
            stolen.shrink(1);
            this.cacofey.setHeldItem(display);
            this.fleeOriginY = this.cacofey.getY();
            this.phase = CacofeyStealGoal.Phase.FLEE;
            this.phaseTimer = 0;
         }
      } else {
         this.reset();
      }
   }

   private void tickFlee() {
      Vec3 awayDir = this.cacofey
         .position()
         .subtract(this.target != null ? this.target.position() : this.cacofey.position())
         .multiply(1.0, 0.0, 1.0)
         .normalize();
      if (awayDir.lengthSqr() < 0.001) {
         awayDir = new Vec3(1.0, 0.0, 0.0);
      }

      double clampedY = Math.min(this.cacofey.getY() + 1.0, this.fleeOriginY + 4.0);
      Vec3 fleeTarget = new Vec3(this.cacofey.getX() + awayDir.x * 8.0, clampedY, this.cacofey.getZ() + awayDir.z * 8.0);
      this.cacofey.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 2.4000000953674316);
      if (++this.phaseTimer >= 80) {
         this.cacofey.getNavigation().stop();
         this.phase = CacofeyStealGoal.Phase.CONSUME;
         this.phaseTimer = 0;
      }
   }

   private void tickConsume() {
      if (++this.phaseTimer >= 20) {
         this.cacofey.setHeldItem(ItemStack.EMPTY);
         this.cacofey.stealCooldown = 1200;
         this.phase = CacofeyStealGoal.Phase.SCAN;
         this.target = null;
         this.phaseTimer = 0;
      }
   }

   private boolean findTarget() {
      List<Player> players = this.cacofey
         .level()
         .getEntitiesOfClass(Player.class, this.cacofey.getBoundingBox().inflate(12.0), p -> !p.isCreative() && !p.isSpectator() && hasEdibleItem(p));
      if (players.isEmpty()) {
         return false;
      } else {
         players.sort((a, b) -> Double.compare(this.cacofey.distanceToSqr(a), this.cacofey.distanceToSqr(b)));
         this.target = players.get(0);
         return true;
      }
   }

   private void reset() {
      this.cacofey.setInspecting(false);
      this.phase = CacofeyStealGoal.Phase.SCAN;
      this.target = null;
      this.phaseTimer = 0;
   }

   private static boolean hasEdibleItem(Player player) {
      return !findEdibleItem(player).isEmpty();
   }

   private static ItemStack findEdibleItem(Player player) {
      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (!stack.isEmpty() && !stack.is((Item)ModItems.GALEBERRIES_COOKIE.get()) && stack.get(DataComponents.FOOD) != null) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   private static enum Phase {
      SCAN,
      APPROACH,
      STEAL,
      FLEE,
      CONSUME;
   }
}
