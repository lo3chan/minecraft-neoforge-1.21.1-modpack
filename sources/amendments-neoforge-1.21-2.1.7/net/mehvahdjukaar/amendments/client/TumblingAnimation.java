package net.mehvahdjukaar.amendments.client;

import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

public class TumblingAnimation {
   private float speedX;
   private float speedY;
   private float speedZ;
   private final Quaternionf currentOrientation = new Quaternionf().identity();
   private final Quaternionf previousOrientation = new Quaternionf().identity();
   private boolean initialized = false;
   private final float minAngularVelMag;
   private final float maxAngularVelMag;
   private final float jitterAmount;

   public TumblingAnimation(float minMagnitude, float maxMagnitude, float maxJitterAmount) {
      this.minAngularVelMag = minMagnitude;
      this.maxAngularVelMag = maxMagnitude;
      this.jitterAmount = maxJitterAmount;
   }

   private void initRotationSpeeds(RandomSource rand) {
      float x = rand.nextFloat() * 2.0F - 1.0F;
      float y = rand.nextFloat() * 2.0F - 1.0F;
      float z = rand.nextFloat() * 2.0F - 1.0F;
      float length = (float)Math.sqrt(x * x + y * y + z * z);
      if (length < 1.0E-6) {
         x = 1.0F;
         y = 0.0F;
         z = 0.0F;
         length = 1.0F;
      }

      x /= length;
      y /= length;
      z /= length;
      float magnitude = this.minAngularVelMag + rand.nextFloat() * (this.maxAngularVelMag - this.minAngularVelMag);
      this.speedX = x * magnitude;
      this.speedY = y * magnitude;
      this.speedZ = z * magnitude;
   }

   public void tick(RandomSource rand) {
      if (!this.initialized) {
         this.initRotationSpeeds(rand);
         this.initialized = true;
      }

      this.speedX = this.speedX + (rand.nextFloat() - 0.5F) * this.jitterAmount;
      this.speedY = this.speedY + (rand.nextFloat() - 0.5F) * this.jitterAmount;
      this.speedZ = this.speedZ + (rand.nextFloat() - 0.5F) * this.jitterAmount;
      float mag = (float)Math.sqrt(this.speedX * this.speedX + this.speedY * this.speedY + this.speedZ * this.speedZ);
      if (mag < this.minAngularVelMag) {
         float scale = this.minAngularVelMag / mag;
         this.speedX *= scale;
         this.speedY *= scale;
         this.speedZ *= scale;
         mag = this.minAngularVelMag;
      } else if (mag > this.maxAngularVelMag) {
         float scale = this.maxAngularVelMag / mag;
         this.speedX *= scale;
         this.speedY *= scale;
         this.speedZ *= scale;
         mag = this.maxAngularVelMag;
      }

      this.previousOrientation.set(this.currentOrientation);
      if (!(mag < 1.0E-6F)) {
         float angleRad = (float)Math.toRadians(mag);
         float axisX = this.speedX / mag;
         float axisY = this.speedY / mag;
         float axisZ = this.speedZ / mag;
         Quaternionf deltaQ = new Quaternionf().fromAxisAngleRad(axisX, axisY, axisZ, angleRad);
         this.currentOrientation.set(deltaQ.mul(this.currentOrientation));
         this.currentOrientation.normalize();
      }
   }

   public Quaternionf getRotation(float partialTicks) {
      return !this.initialized ? new Quaternionf().identity() : this.previousOrientation.nlerp(this.currentOrientation, partialTicks, new Quaternionf());
   }
}
