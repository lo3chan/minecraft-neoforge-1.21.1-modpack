package net.diebuddies.physics.ocean;

public class RippleParticle {
   public double x;
   public double y;
   public double z;
   public double xo;
   public double yo;
   public double zo;
   public double vx;
   public double vy;
   public double vz;
   public float alpha;
   public float state;
   public int lifetime;
   public int startLifetime;
   public float baseAlpha;
   public float scale;

   public RippleParticle(int lifetime, double x, double y, double z, double vx, double vy, double vz) {
      this.lifetime = lifetime;
      this.startLifetime = lifetime;
      this.x = x + vx;
      this.y = y + vy;
      this.z = z + vz;
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.vx = vx;
      this.vy = vy;
      this.vz = vz;
      this.baseAlpha = 1.0F;
      this.alpha = 0.0F;
      this.scale = 0.75F;
   }

   public RippleParticle() {
   }

   public void update() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.x = this.x + this.vx;
      this.y = this.y + this.vy;
      this.z = this.z + this.vz;
      this.lifetime--;
      double x = (double)this.lifetime / this.startLifetime * 1.5;
      this.alpha = (float)(3.0 * (x * x) - 2.0 * (x * x * x)) * this.baseAlpha;
   }
}
