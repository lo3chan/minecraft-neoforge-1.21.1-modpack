package net.mehvahdjukaar.amendments.client;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ElasticAnimation {
   private static final int MAX_SQUISH_TICKS = 10;
   private float squosh = 1.0F;
   private float oldSquosh = 1.0F;
   private float squish = 1.0F;
   private float oldSquish = 1.0F;
   private int squishTicks = 0;

   public void tick(Vec3 movement) {
      this.squishTicks = Math.max(0, this.squishTicks - 1);
      double speed = Mth.clamp(movement.lengthSqr() * 3.0, 0.0, 1.0);
      this.oldSquosh = this.squosh;
      this.oldSquish = this.squish;
      this.squosh = (float)Math.max(0.3, 1.0 + speed - 1.0F * this.squishTicks / 10.0F);
      this.squish = 1.0F / Mth.sqrt(this.squosh);
   }

   public Vector3f getScale(float partialTicks) {
      float squishFactor = Mth.lerp(partialTicks, this.oldSquish, this.squish);
      float squoshFactor = Mth.lerp(partialTicks, this.oldSquosh, this.squosh);
      return new Vector3f(squishFactor, squishFactor, squoshFactor);
   }

   public void setSquishedDown() {
      this.squishTicks = 10;
   }
}
