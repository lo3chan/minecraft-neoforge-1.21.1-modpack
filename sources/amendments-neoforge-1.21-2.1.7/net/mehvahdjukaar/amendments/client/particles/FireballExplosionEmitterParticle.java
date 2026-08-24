package net.mehvahdjukaar.amendments.client.particles;

import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class FireballExplosionEmitterParticle extends NoRenderParticle {
   private final double size;
   private int life;

   FireballExplosionEmitterParticle(ClientLevel clientLevel, double x, double y, double z, double size) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
      this.size = size + 1.0;
   }

   public void tick() {
      int lifeTime = 8;

      for (int i = 0; i < 6; i++) {
         double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * this.size;
         double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * this.size;
         double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * this.size;
         this.level.addParticle((ParticleOptions)ModRegistry.FIREBALL_EXPLOSION_PARTICLE.get(), d, e, f, (float)this.life / lifeTime, 0.0, 0.0);
      }

      this.life++;
      if (this.life == lifeTime) {
         this.remove();
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet sprite) {
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double size, double ySpeed, double zSpeed) {
         return new FireballExplosionEmitterParticle(level, x, y, z, size);
      }
   }
}
