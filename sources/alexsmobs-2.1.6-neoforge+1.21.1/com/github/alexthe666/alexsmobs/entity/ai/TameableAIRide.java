package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TameableAIRide extends Goal {
   private final PathfinderMob tameableEntity;
   private LivingEntity player;
   private final double speed;
   private final boolean strafe;

   public TameableAIRide(PathfinderMob dragon, double speed) {
      this(dragon, speed, true);
   }

   public TameableAIRide(PathfinderMob dragon, double speed, boolean strafe) {
      this.tameableEntity = dragon;
      this.speed = speed;
      this.strafe = strafe;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.tameableEntity.getControllingPassenger() instanceof Player && this.tameableEntity.isVehicle()) {
         this.player = (Player)this.tameableEntity.getControllingPassenger();
         return true;
      } else {
         this.tameableEntity.setSprinting(false);
         return false;
      }
   }

   public void start() {
      this.tameableEntity.getNavigation().stop();
   }

   public void tick() {
      AMCompat.setMaxUpStep(this.tameableEntity, 1.0F);
      this.tameableEntity.getNavigation().stop();
      this.tameableEntity.setTarget(null);
      double x = this.tameableEntity.getX();
      double y = this.tameableEntity.getY();
      double z = this.tameableEntity.getZ();
      if (this.strafe) {
         this.tameableEntity.xxa = AMCompat.riderStrafe(this.player) * 0.15F;
      }

      if (this.shouldMoveForward() && this.tameableEntity.isVehicle()) {
         this.tameableEntity.setSprinting(true);
         Vec3 lookVec = this.player.getLookAngle();
         if (this.shouldMoveBackwards()) {
            lookVec = lookVec.yRot(3.1415927F);
         }

         x += lookVec.x * 10.0;
         z += lookVec.z * 10.0;
         y += this.modifyYPosition(lookVec.y);
         this.tameableEntity.getMoveControl().setWantedPosition(x, y, z, this.speed);
      } else {
         this.tameableEntity.setSprinting(false);
      }
   }

   public double modifyYPosition(double lookVecY) {
      return this.tameableEntity instanceof FlyingAnimal ? lookVecY * 10.0 : 0.0;
   }

   public boolean shouldMoveForward() {
      return AMCompat.riderForward(this.player) != 0.0F;
   }

   public boolean shouldMoveBackwards() {
      return AMCompat.riderForward(this.player) < 0.0F;
   }
}
