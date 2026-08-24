package net.joefoxe.hexerei.particle;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlowingLeavesParticle extends TextureSheetParticle {
   private static final float ACCELERATION_SCALE = 0.0025F;
   private static final int INITIAL_LIFETIME = 300;
   private static final int CURVE_ENDPOINT_TIME = 300;
   private static final float FALL_ACC = 0.25F;
   private static final float WIND_BIG = 2.0F;
   private float rotSpeed;
   private final float particleRandom;
   private final float spinAcceleration;

   protected FlowingLeavesParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSpriteSet) {
      super(pLevel, pX, pY, pZ);
      this.setSprite(pSpriteSet.get(this.random.nextInt(12), 12));
      this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
      this.particleRandom = this.random.nextFloat();
      this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
      this.lifetime = 300;
      this.gravity = 7.5E-4F;
      float f = this.random.nextBoolean() ? 0.05F : 0.075F;
      this.quadSize = f;
      this.setSize(f, f);
      this.friction = 1.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      }

      if (!this.removed) {
         float f = 300 - this.lifetime;
         float f1 = Math.min(f / 300.0F, 1.0F);
         double d0 = Math.cos(Math.toRadians(this.particleRandom * 60.0F)) * 2.0 * Math.pow(f1, 1.25);
         double d1 = Math.sin(Math.toRadians(this.particleRandom * 60.0F)) * 2.0 * Math.pow(f1, 1.25);
         this.xd += d0 * 0.0024999999441206455;
         this.zd += d1 * 0.0024999999441206455;
         this.yd = this.yd - this.gravity;
         this.rotSpeed = this.rotSpeed + this.spinAcceleration / 20.0F;
         this.oRoll = this.roll;
         this.roll = this.roll + this.rotSpeed / 20.0F;
         this.move(this.xd, this.yd, this.zd);
         if (this.onGround || this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0)) {
            this.remove();
         }

         if (!this.removed) {
            this.xd = this.xd * this.friction;
            this.yd = this.yd * this.friction;
            this.zd = this.zd * this.friction;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random rand = new Random();
         float colorOffset = rand.nextFloat() * 0.4F;
         FlowingLeavesParticle flowingLeaves = new FlowingLeavesParticle(worldIn, x, y, z, this.spriteSet);
         flowingLeaves.pickSprite(this.spriteSet);
         flowingLeaves.setColor(1.0F - colorOffset, 1.0F - colorOffset, 1.0F - colorOffset);
         return flowingLeaves;
      }
   }
}
