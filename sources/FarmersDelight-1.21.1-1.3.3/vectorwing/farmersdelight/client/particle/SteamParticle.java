package vectorwing.farmersdelight.client.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class SteamParticle extends TextureSheetParticle {
   protected SteamParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(level, x, y, z);
      this.scale(2.0F);
      this.setSize(0.25F, 0.25F);
      this.lifetime = this.random.nextInt(50) + 80;
      this.gravity = 3.0E-6F;
      this.xd = motionX;
      this.yd = motionY + this.random.nextFloat() / 500.0F;
      this.zd = motionZ;
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
         this.xd = this.xd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
         this.zd = this.zd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
         this.yd = this.yd - this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.02F;
         }
      } else {
         this.remove();
      }
   }

   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         SteamParticle particle = new SteamParticle(level, x, y + 0.3, z, xSpeed, ySpeed, zSpeed);
         particle.setAlpha(0.6F);
         particle.pickSprite(this.spriteSet);
         return particle;
      }
   }
}
