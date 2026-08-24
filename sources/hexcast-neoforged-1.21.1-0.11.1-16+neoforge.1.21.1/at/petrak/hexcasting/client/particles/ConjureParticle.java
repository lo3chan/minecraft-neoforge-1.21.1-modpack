package at.petrak.hexcasting.client.particles;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConjureParticle extends TextureSheetParticle {
   private static final Random RANDOM = new Random();
   private final SpriteSet sprites;

   ConjureParticle(ClientLevel pLevel, double x, double y, double z, double dx, double dy, double dz, SpriteSet pSprites, int color) {
      super(pLevel, x, y, z, dx, dy, dz);
      this.quadSize *= 0.9F;
      this.setParticleSpeed(dx, dy, dz);
      int r = ARGB32.red(color);
      int g = ARGB32.green(color);
      int b = ARGB32.blue(color);
      this.setColor(r / 255.0F, g / 255.0F, b / 255.0F);
      this.setAlpha(0.3F);
      this.friction = 0.96F;
      this.gravity = dy != 0.0 && dx != 0.0 && dz != 0.0 ? -0.01F : 0.0F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = pSprites;
      this.roll = RANDOM.nextFloat(360.0F);
      this.oRoll = this.roll;
      this.lifetime = (int)(64.0 / ((Math.random() + 3.0) * 0.25));
      this.hasPhysics = false;
      this.setSpriteFromAge(pSprites);
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      this.alpha = 1.0F - (float)this.age / this.lifetime;
      this.alpha *= 0.3F;
      this.quadSize *= 0.96F;
   }

   public void setSpriteFromAge(@NotNull SpriteSet pSprite) {
      if (!this.removed) {
         int age = this.age * 4;
         if (age > this.lifetime) {
            age /= 4;
         }

         this.setSprite(pSprite.get(age, this.lifetime));
      }
   }

   public static class Provider implements ParticleProvider<ConjureParticleOptions> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprites) {
         this.sprite = pSprites;
      }

      @Nullable
      public Particle createParticle(
         ConjureParticleOptions type, ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         return new ConjureParticle(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite, type.color());
      }
   }
}
