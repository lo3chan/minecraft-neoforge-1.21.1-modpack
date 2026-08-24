package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class LeavesParticle extends SimpleAnimatedParticle {
   protected LeavesParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
      super(level, x, y, z, sprites, 0.01F);
      this.hasPhysics = false;
      this.friction = 0.96F;
      this.gravity = 0.0F;
      if (velocityX == 0.0 && velocityY == 0.0 && velocityZ == 0.0) {
         this.xd = (this.random.nextDouble() - 0.5) * 0.02;
         this.yd = this.random.nextDouble() * 0.02;
         this.zd = (this.random.nextDouble() - 0.5) * 0.02;
      } else {
         this.xd = velocityX;
         this.yd = velocityY;
         this.zd = velocityZ;
      }

      this.quadSize = this.quadSize * (0.2F + this.random.nextFloat() * 0.4F);
      this.lifetime = 14 + this.random.nextInt(6);
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
         return new LeavesParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
      }
   }
}
