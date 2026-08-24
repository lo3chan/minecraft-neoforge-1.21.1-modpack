package net.cibernet.alchemancy.client.particle;

import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SparkParticle extends DustParticleBase<SparkParticleOptions> {
   protected SparkParticle(
      ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SparkParticleOptions options, SpriteSet sprites
   ) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
      float f = (float)Math.random() * 0.4F - 0.2F;
      this.rCol = Math.clamp(options.getColor().x() + f, 0.0F, 1.0F);
      this.gCol = Math.clamp(options.getColor().y() + f, 0.0F, 1.0F);
      this.bCol = Math.clamp(options.getColor().z() + f, 0.0F, 1.0F);
      if (!options.stationary) {
         this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SparkParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SparkParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.sprites);
      }
   }
}
