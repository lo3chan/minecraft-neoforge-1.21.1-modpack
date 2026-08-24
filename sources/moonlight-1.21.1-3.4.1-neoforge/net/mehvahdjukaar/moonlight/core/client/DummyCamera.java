package net.mehvahdjukaar.moonlight.core.client;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class DummyCamera extends Camera {
   public void setPosition(double pX, double pY, double pZ) {
      super.setPosition(pX, pY, pZ);
   }

   public void setPosition(Vec3 pPos) {
      super.setPosition(pPos);
   }

   public void setPosition(BlockPos pPos) {
      super.setPosition(Vec3.atCenterOf(pPos));
   }

   public void setRotation(float yRot, float xRot) {
      super.setRotation(yRot, xRot);
   }
}
