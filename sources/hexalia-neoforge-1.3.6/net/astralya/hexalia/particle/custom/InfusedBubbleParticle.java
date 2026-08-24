package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class InfusedBubbleParticle extends SimpleAnimatedParticle {
   protected InfusedBubbleParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
      super(level, x, y, z, sprites, 0.01F);
      this.xd = velocityX;
      this.yd = velocityY;
      this.zd = velocityZ;
      this.quadSize = this.quadSize * (0.5F + this.random.nextInt(1));
      this.lifetime = 10 + this.random.nextInt(12);
      this.setColor(15916745);
      this.setSpriteFromAge(sprites);
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().move(x, y, z));
      this.setLocationFromBoundingbox();
   }

   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ
      ) {
         return new InfusedBubbleParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
      }
   }
}
