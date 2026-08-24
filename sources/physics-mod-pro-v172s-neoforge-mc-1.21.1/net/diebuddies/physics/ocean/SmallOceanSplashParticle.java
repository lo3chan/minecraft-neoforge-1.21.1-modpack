package net.diebuddies.physics.ocean;

import net.diebuddies.math.Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SmallOceanSplashParticle extends OceanSplashParticle {
   public SmallOceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
      super(clientLevel, x, y, z, vx, vy, vz);
      float size = Math.random() * 0.6F + 0.15F;
      this.setSize(size, size);
      this.quadSize = size;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz
      ) {
         SmallOceanSplashParticle particle = new SmallOceanSplashParticle(clientLevel, x, y, z, vx, vy, vz);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }
}
