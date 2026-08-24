package net.diebuddies.physics.ocean;

public class RainParticle extends RippleParticle {
   public RainParticle(int lifetime, float scale, double x, double y, double z) {
      this.state = 0.1F;
      this.lifetime = lifetime;
      this.startLifetime = lifetime;
      this.x = x + this.vx;
      this.y = y + this.vy;
      this.z = z + this.vz;
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.baseAlpha = 1.0F;
      this.alpha = 0.0F;
      this.scale = scale;
   }

   @Override
   public void update() {
      super.update();
      this.state = 1.0F - (float)this.lifetime / this.startLifetime + 0.1F;
   }
}
