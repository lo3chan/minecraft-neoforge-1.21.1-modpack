package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ParticleBunfungusTransformation extends AMQuadParticle {
   private final SpriteSet sprites;

   public ParticleBunfungusTransformation(
      ClientLevel p_107483_, double p_107484_, double p_107485_, double p_107486_, double p_107487_, double p_107488_, double p_107489_, SpriteSet p_107490_
   ) {
      super(p_107483_, p_107484_, p_107485_, p_107486_, 0.0, 0.0, 0.0);
      this.friction = 0.96F;
      this.sprites = p_107490_;
      float f = 2.5F;
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.xd += p_107487_;
      this.yd += p_107488_;
      this.zd += p_107489_;
      int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
      this.lifetime = (int)Math.max(i * 2.5F, 1.0F);
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107490_);
      this.setAlpha((float)(Math.random() * 0.30000001192092896 + 0.699999988079071));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public float getQuadSize(float p_107504_) {
      return this.quadSize * Mth.clamp((this.age + p_107504_) / this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         Player player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0, false);
         if (player != null) {
            double d0 = player.getY();
            if (this.y > d0) {
               this.y = this.y + (d0 - this.y) * 0.2;
               this.yd = this.yd + (player.getDeltaMovement().y - this.yd) * 0.2;
               this.setPos(this.x, this.y, this.z);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Factory(SpriteSet p_107528_) {
         this.sprites = p_107528_;
      }

      public Particle createParticle(
         SimpleParticleType p_107539_,
         ClientLevel p_107540_,
         double p_107541_,
         double p_107542_,
         double p_107543_,
         double p_107544_,
         double p_107545_,
         double p_107546_
      ) {
         ParticleBunfungusTransformation particle = new ParticleBunfungusTransformation(
            p_107540_, p_107541_, p_107542_, p_107543_, p_107544_, p_107545_, p_107546_, this.sprites
         );
         particle.setColor(0.427451F, 0.4470588F, 0.5764706F);
         return particle;
      }
   }
}
