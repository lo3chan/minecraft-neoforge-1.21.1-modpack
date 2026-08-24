package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class SporeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final double baseX;
   private final double baseY;
   private final double baseZ;
   private final double swayOffset;
   private final double verticalOffset;

   protected SporeParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      SpriteSet sprites,
      float red,
      float green,
      float blue
   ) {
      super(level, x, y, z, velocityX, velocityY, velocityZ);
      this.sprites = sprites;
      this.baseX = x;
      this.baseY = y;
      this.baseZ = z;
      this.swayOffset = this.random.nextDouble() * 3.141592653589793 * 2.0;
      this.verticalOffset = this.random.nextDouble() * 3.141592653589793 * 2.0;
      this.quadSize = this.quadSize * (0.12F + this.random.nextFloat() * 0.05F);
      this.lifetime = 80 + this.random.nextInt(40);
      this.gravity = 0.0F;
      this.friction = 1.0F;
      this.hasPhysics = false;
      this.alpha = 0.0F;
      this.setColor(red, green, blue);
      this.pickSprite(sprites);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.pickSprite(this.sprites);
         float ageProgress = (float)this.age / this.lifetime;
         this.alpha = ageProgress < 0.15F ? ageProgress / 0.15F : (ageProgress > 0.75F ? Mth.clamp((1.0F - ageProgress) / 0.25F, 0.0F, 1.0F) : 1.0F);
         double swayX = Math.sin(this.age * 0.06 + this.swayOffset) * 0.03;
         double swayZ = Math.cos(this.age * 0.06 + this.swayOffset) * 0.03;
         double bobY = Math.sin(this.age * 0.04 + this.verticalOffset) * 0.01;
         this.x = this.baseX + swayX;
         this.y = this.baseY + bobY;
         this.z = this.baseZ + swayZ;
      }
   }

   public static class Factory implements ParticleProvider<ColoredSporeParticleOptions> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(
         ColoredSporeParticleOptions options, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ
      ) {
         return new SporeParticle(
            level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet, options.color().x(), options.color().y(), options.color().z()
         );
      }
   }
}
