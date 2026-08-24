package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

public class AnimalAIRideParent extends Goal {
   private final Animal childAnimal;
   private Animal parentAnimal;
   private final double moveSpeed;
   private int delayCounter;

   public AnimalAIRideParent(Animal animal, double speed) {
      this.childAnimal = animal;
      this.moveSpeed = speed;
   }

   public boolean canUse() {
      if (this.childAnimal.getAge() < 0 && !this.childAnimal.isPassenger()) {
         List<? extends Animal> list = this.childAnimal
            .level()
            .getEntitiesOfClass(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().inflate(8.0, 4.0, 8.0));
         Animal animalentity = null;
         double d0 = 1.7976931348623157E308;

         for (Animal animalentity1 : list) {
            if (animalentity1.getAge() >= 0 && animalentity1.getPassengers().isEmpty()) {
               double d1 = this.childAnimal.distanceToSqr(animalentity1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  animalentity = animalentity1;
               }
            }
         }

         if (animalentity == null) {
            return false;
         } else if (d0 < 2.0) {
            return false;
         } else {
            this.parentAnimal = animalentity;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (this.childAnimal.getAge() >= 0) {
         return false;
      } else if (this.parentAnimal != null && this.parentAnimal.isAlive() && this.parentAnimal.getPassengers().isEmpty()) {
         double d0 = this.childAnimal.distanceToSqr(this.parentAnimal);
         return !(d0 < 2.0) && !(d0 > 256.0) && !this.childAnimal.isPassengerOfSameVehicle(this.parentAnimal);
      } else {
         return false;
      }
   }

   public void start() {
      this.delayCounter = 0;
   }

   public void stop() {
      this.parentAnimal = null;
   }

   public void tick() {
      if (--this.delayCounter <= 0) {
         this.delayCounter = 10;
         this.childAnimal.getNavigation().moveTo(this.parentAnimal, this.moveSpeed);
      }

      if (this.childAnimal.distanceTo(this.parentAnimal) < 2.0) {
         AMCompat.startRiding(this.childAnimal, this.parentAnimal, false);
         this.stop();
      }
   }
}
