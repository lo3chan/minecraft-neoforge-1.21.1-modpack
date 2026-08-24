package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ParticleSunbirdFeather extends SimpleAnimatedParticle {
   private float initialRoll = 0.0F;

   private ParticleSunbirdFeather(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
      super(world, x, y, z, sprites, 0.0F);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.quadSize = 0.15F + this.random.nextFloat() * 0.2F;
      this.lifetime = 20 + this.random.nextInt(20);
      this.gravity = 0.02F;
      this.pickSprite(sprites);
      float f = Mth.sqrt((float)(xd * xd + zd * zd));
      float f1 = -((float)Mth.atan2(yd, f)) + Maths.rad(135.0);
      this.roll = f1 * 2.0F;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.xd *= 0.8;
      this.yd *= 0.8;
      this.zd *= 0.8;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.oRoll = this.roll;
         if (!this.onGround) {
            this.roll = this.roll + (0.0F + (float)Math.sin(this.age * 0.3F) * 0.5F * ((float)this.age / this.lifetime));
         }

         this.move(this.xd, this.yd, this.zd);
         this.yd = this.yd - this.gravity;
      }
   }

   public int getLightColor(float p_189214_1_) {
      int lvt_2_1_ = super.getLightColor(p_189214_1_);
      int lvt_4_1_ = lvt_2_1_ >> 16 & 0xFF;
      return 240 | lvt_4_1_ << 16;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleSunbirdFeather(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
