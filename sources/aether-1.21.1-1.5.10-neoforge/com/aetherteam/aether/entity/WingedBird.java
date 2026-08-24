package com.aetherteam.aether.entity;

public interface WingedBird extends NotGrounded {
   float getWingRotation();

   void setWingRotation(float var1);

   float getPrevWingRotation();

   void setPrevWingRotation(float var1);

   float getWingDestPos();

   void setWingDestPos(float var1);

   float getPrevWingDestPos();

   void setPrevWingDestPos(float var1);

   default void animateWings() {
      this.setPrevWingRotation(this.getWingRotation());
      this.setPrevWingDestPos(this.getWingDestPos());
      if (!this.isEntityOnGround()) {
         this.setWingDestPos(this.getWingDestPos() + 0.45F);
         this.setWingDestPos(Math.min(1.0F, Math.max(0.01F, this.getWingDestPos())));
      } else {
         this.setWingDestPos(0.0F);
         this.setWingRotation(0.0F);
      }

      this.setWingRotation(this.getWingRotation() + 3.0F);
   }
}
