package com.aetherteam.aether.entity.ai.controller;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;

public class FallingMoveControl extends MoveControl {
   public FallingMoveControl(Mob mob) {
      super(mob);
   }

   public void tick() {
      if (this.operation == Operation.JUMPING) {
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         if (this.mob.onGround()) {
            this.operation = Operation.WAIT;
         } else {
            this.operation = Operation.MOVE_TO;
         }
      } else {
         super.tick();
      }
   }
}
