package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SparkleParticle extends SimpleAnimatedParticle {
   private final float baseSize;
   private final float shimmerSpeed;
   private final float shimmerStrength;
   private final float yBoost;
   private final double startX;
   private final double startZ;

   protected SparkleParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
      super(level, x, y, z, sprites, 0.01F);
      this.hasPhysics = false;
      this.friction = 0.96F;
      this.gravity = 0.0F;
      this.startX = x;
      this.startZ = z;
      if (velocityX == 0.0 && velocityY == 0.0 && velocityZ == 0.0) {
         this.xd = (this.random.nextDouble() - 0.5) * 0.004;
         this.yd = 0.008 + this.random.nextDouble() * 0.01;
         this.zd = (this.random.nextDouble() - 0.5) * 0.004;
      } else {
         this.xd = velocityX;
         this.yd = velocityY;
         this.zd = velocityZ;
      }

      this.baseSize = 0.08F + this.random.nextFloat() * 0.08F;
      this.quadSize = this.quadSize * this.baseSize;
      this.lifetime = 18 + this.random.nextInt(14);
      this.shimmerSpeed = 0.18F + this.random.nextFloat() * 0.25F;
      this.shimmerStrength = 0.01F + this.random.nextFloat() * 0.01F;
      this.yBoost = 0.002F + this.random.nextFloat() * 0.004F;
      this.setColor(15916745);
      this.alpha = 0.0F;
      this.setSpriteFromAge(sprites);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         float ageProgress = (float)this.age / this.lifetime;
         float fadeIn = Mth.clamp(ageProgress / 0.15F, 0.0F, 1.0F);
         float fadeOut = Mth.clamp((1.0F - ageProgress) / 0.35F, 0.0F, 1.0F);
         this.alpha = Math.min(fadeIn, fadeOut);
         float pulse = 0.85F + 0.25F * Mth.sin((this.age + this.random.nextFloat()) * 0.35F);
         this.quadSize = this.baseSize * pulse;
         double wobble = Mth.sin((this.age + this.random.nextFloat()) * this.shimmerSpeed) * this.shimmerStrength;
         this.x = this.startX + wobble + (this.x - this.startX) * 0.98;
         this.z = this.startZ + wobble + (this.z - this.startZ) * 0.98;
         this.yd = this.yd + this.yBoost;
      }
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
         return new SparkleParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
      }
   }
}
