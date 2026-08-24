package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GorillaAIChargeLooker extends Goal {
   private final EntityGorilla gorilla;
   private final double range = 20.0;
   private Player starer;
   private final double speed;
   private int runDelay = 0;

   public GorillaAIChargeLooker(EntityGorilla gorilla, double speed) {
      this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
      this.gorilla = gorilla;
      this.speed = speed;
   }

   public boolean canUse() {
      if (this.gorilla.isSilverback() && !this.gorilla.isTame() && this.runDelay-- == 0) {
         this.runDelay = 100 + this.gorilla.getRandom().nextInt(200);
         List<Player> playerList = this.gorilla
            .level()
            .getEntitiesOfClass(Player.class, this.gorilla.getBoundingBox().inflate(20.0, 20.0, 20.0), EntitySelector.NO_SPECTATORS);
         Player closestPlayer = null;

         for (Player player : playerList) {
            if (this.isLookingAtMe(player) && (closestPlayer == null || player.distanceTo(this.gorilla) < closestPlayer.distanceTo(this.gorilla))) {
               closestPlayer = player;
            }
         }

         this.starer = closestPlayer;
         return this.starer != null;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.starer != null && this.gorilla.isAlive();
   }

   public void stop() {
      this.starer = null;
      this.gorilla.setSprinting(false);
      this.runDelay = 300 + this.gorilla.getRandom().nextInt(200);
   }

   public void tick() {
      this.gorilla.setOrderedToSit(false);
      this.gorilla.poundChestCooldown = 50;
      if (this.gorilla.distanceTo(this.starer) > 1.0F + this.starer.getBbWidth() + this.gorilla.getBbWidth()) {
         this.gorilla.getNavigation().moveTo(this.starer, this.speed);
         this.gorilla.setSprinting(!this.gorilla.isSitting() && !this.gorilla.isStanding());
      } else {
         this.gorilla.getNavigation().stop();
         this.gorilla.lookAt(this.starer, 180.0F, 30.0F);
         this.gorilla.setSprinting(false);
         if (this.gorilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.gorilla.setStanding(true);
            this.gorilla.maxStandTime = 45;
            this.gorilla.setAnimation(EntityGorilla.ANIMATION_POUNDCHEST);
         }

         if (this.gorilla.getAnimation() == EntityGorilla.ANIMATION_POUNDCHEST && this.gorilla.getAnimationTick() >= 10) {
            this.stop();
         }
      }
   }

   private boolean isLookingAtMe(Player player) {
      Vec3 vec3 = player.getViewVector(1.0F).normalize();
      Vec3 vec31 = new Vec3(this.gorilla.getX() - player.getX(), this.gorilla.getEyeY() - player.getEyeY(), this.gorilla.getZ() - player.getZ());
      double d0 = vec31.length();
      vec31 = vec31.normalize();
      double d1 = vec3.dot(vec31);
      return d1 > 1.0 - 0.025 / d0 && player.hasLineOfSight(this.gorilla);
   }
}
