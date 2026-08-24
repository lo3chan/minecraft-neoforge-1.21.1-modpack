package vectorwing.farmersdelight.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class StarParticle extends TextureSheetParticle {
   protected StarParticle(ClientLevel level, double posX, double posY, double posZ) {
      super(level, posX, posY, posZ, 0.0, 0.0, 0.0);
      this.xd *= 0.009999999776482582;
      this.yd *= 0.009999999776482582;
      this.zd *= 0.009999999776482582;
      this.yd += 0.1;
      this.quadSize *= 1.5F;
      this.lifetime = 16;
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float scaleFactor) {
      return this.quadSize * Mth.clamp((this.age + scaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
         }

         this.xd *= 0.8600000143051147;
         this.yd *= 0.8600000143051147;
         this.zd *= 0.8600000143051147;
         if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
         }
      }
   }

   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         StarParticle particle = new StarParticle(level, x, y + 0.3, z);
         particle.pickSprite(this.spriteSet);
         particle.setColor(1.0F, 1.0F, 1.0F);
         return particle;
      }
   }
}
