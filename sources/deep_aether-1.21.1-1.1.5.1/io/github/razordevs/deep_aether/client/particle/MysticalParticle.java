package io.github.razordevs.deep_aether.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MysticalParticle extends SimpleAnimatedParticle {
   MysticalParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
      super(level, xCoord, yCoord, zCoord, sprite, 0.0125F);
      this.xd = xSpeed;
      this.yd = ySpeed;
      this.zd = zSpeed;
      this.quadSize *= 0.75F;
      this.lifetime = 200 + this.random.nextInt(100);
      this.setFadeColor(15916745);
      this.setSpriteFromAge(sprite);
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().move(x, y, z));
      this.setLocationFromBoundingbox();
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         SimpleParticleType type, ClientLevel level, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new MysticalParticle(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, this.sprites);
      }
   }
}
