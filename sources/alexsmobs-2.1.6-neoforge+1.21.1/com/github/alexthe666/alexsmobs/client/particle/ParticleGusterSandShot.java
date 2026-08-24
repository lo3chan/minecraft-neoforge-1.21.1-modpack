package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ParticleGusterSandShot extends AMQuadParticle {
   private ParticleGusterSandShot(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant) {
      super(world, x, y, z);
      int color = ParticleGusterSandSpin.selectColor(variant, this.random);
      float lvt_18_1_ = (color >> 16 & 0xFF) / 255.0F;
      float lvt_19_1_ = (color >> 8 & 0xFF) / 255.0F;
      float lvt_20_1_ = (color & 0xFF) / 255.0F;
      this.setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
      this.xd = (float)motionX;
      this.yd = (float)motionY;
      this.zd = (float)motionZ;
      this.quadSize = this.quadSize * (0.6F + this.random.nextFloat() * 1.4F);
      this.lifetime = 10 + this.random.nextInt(15);
      this.gravity = 0.5F;
   }

   public void tick() {
      super.tick();
      this.yd = this.yd - (0.004 + 0.04 * this.gravity);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
         p.pickSprite(this.spriteSet);
         return p;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FactoryRed implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public FactoryRed(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1);
         p.pickSprite(this.spriteSet);
         return p;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FactorySoul implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public FactorySoul(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 2);
         p.pickSprite(this.spriteSet);
         return p;
      }
   }
}
