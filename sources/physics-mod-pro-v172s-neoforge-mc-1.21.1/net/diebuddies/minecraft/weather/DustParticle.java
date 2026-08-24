package net.diebuddies.minecraft.weather;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class DustParticle extends SnowParticle {
   public DustParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
      super(clientLevel, x, y, z, vx, vy, vz);
      this.gravity = 0.002F;
      this.dampingX = 0.999;
      this.dampingY = 0.98;
      this.dampingZ = 0.999;
      double greyscale = Math.random() * 0.3 + 0.7;
      int color = (int)(255.0 * greyscale);
      this.setColor(color, color, color, (int)(255.0F * ConfigClient.particleDustOpacity));
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz
      ) {
         DustParticle particle = new DustParticle(clientLevel, x, y, z, vx, vy, vz);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }
}
