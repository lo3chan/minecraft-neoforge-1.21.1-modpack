package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ParticleWormPortal extends SimpleAnimatedParticle {
   private ParticleWormPortal(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
      super(world, x, y, z, sprites, 0.0F);
      this.xd = (float)motionX;
      this.yd = (float)motionY;
      this.zd = (float)motionZ;
      this.quadSize = 0.35F;
      this.lifetime = 10 + this.random.nextInt(12);
      this.gravity = 0.0F;
      this.setSpriteFromAge(sprites);
   }

   public int getLightColor(float p_189214_1_) {
      int lvt_2_1_ = super.getLightColor(p_189214_1_);
      int lvt_4_1_ = lvt_2_1_ >> 16 & 0xFF;
      return 240 | lvt_4_1_ << 16;
   }

   public void tick() {
      super.tick();
      this.oRoll = this.roll;
      this.xd *= 0.8;
      this.yd *= 0.8;
      this.zd *= 0.8;
      this.roll += 0.25F;
      this.setSpriteFromAge(this.sprites);
      this.quadSize = 0.35F * (1.0F - (float)this.age / this.lifetime);
      this.setAlpha(1.0F - (float)this.age / this.lifetime);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleWormPortal(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
