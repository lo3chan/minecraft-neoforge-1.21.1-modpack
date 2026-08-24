package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.entity.ai.navigator.FallPathNavigation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class WingedAnimal extends MountableAnimal {
   private float wingFold;
   private float wingAngle;

   public WingedAnimal(EntityType<? extends Animal> type, Level level) {
      super(type, level);
   }

   protected PathNavigation createNavigation(Level level) {
      return new FallPathNavigation(this, level);
   }

   @Override
   public void tick() {
      super.tick();
      AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
      if (gravity != null) {
         double fallSpeed = Math.max(gravity.getValue() * -1.25, -0.1);
         if (this.getDeltaMovement().y() < fallSpeed && !this.playerTriedToCrouch()) {
            this.setDeltaMovement(this.getDeltaMovement().x(), fallSpeed, this.getDeltaMovement().z());
            this.hasImpulse = true;
            this.setEntityOnGround(false);
         }
      }
   }

   @Override
   public void riderTick() {
      super.riderTick();
      if (this.getControllingPassenger() instanceof Player) {
         this.checkSlowFallDistance();
      }
   }

   public float getWingFold() {
      return this.wingFold;
   }

   public void setWingFold(float wingFold) {
      this.wingFold = wingFold;
   }

   public float getWingAngle() {
      return this.wingAngle;
   }

   public void setWingAngle(float wingAngle) {
      this.wingAngle = wingAngle;
   }

   @Override
   public float getFlyingSpeed() {
      return this.isEffectiveAi() && !this.onGround() && this.getPassengers().isEmpty()
         ? this.getSpeed() * (0.24F / (float)Math.pow(0.9100000262260437, 3.0))
         : super.getFlyingSpeed();
   }

   @Override
   public boolean canJump() {
      return this.isSaddled();
   }

   public int getMaxFallDistance() {
      return this.onGround() ? super.getMaxFallDistance() : 14;
   }
}
