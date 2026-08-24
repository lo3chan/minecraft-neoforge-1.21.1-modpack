package net.mehvahdjukaar.amendments.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor.ARGB32;

public class ColoredSplashParticle extends WaterDropParticle {
   private final int lightLevel;

   ColoredSplashParticle(ClientLevel clientLevel, double d, double e, double f, int light) {
      super(clientLevel, d, e, f);
      this.gravity = 0.04F;
      this.lightLevel = light;
   }

   protected int getLightColor(float partialTick) {
      int lightColor = super.getLightColor(partialTick);
      int sky = LightTexture.sky(lightColor);
      int block = LightTexture.block(lightColor);
      return LightTexture.pack(Math.max(this.lightLevel, block), sky);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double color, double unused, double light) {
         int intColor = (int)color;
         float r = ARGB32.red(intColor) / 255.0F;
         float g = ARGB32.green(intColor) / 255.0F;
         float b = ARGB32.blue(intColor) / 255.0F;
         ColoredSplashParticle p = new ColoredSplashParticle(level, x, y, z, (int)light);
         p.setColor(r, g, b);
         p.pickSprite(this.sprites);
         return p;
      }
   }
}
