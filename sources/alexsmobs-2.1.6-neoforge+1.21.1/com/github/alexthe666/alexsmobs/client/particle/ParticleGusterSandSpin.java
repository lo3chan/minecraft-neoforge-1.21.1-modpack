package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ParticleGusterSandSpin extends AMQuadParticle {
   private float targetX;
   private float targetY;
   private float targetZ;
   private float distX;
   private float distZ;
   private static final int[] POSSIBLE_COLORS = new int[]{15975305, 15709560, 16307354, 16770733, 16777165};
   private static final int[] POSSIBLE_COLORS_RED = new int[]{13988150, 13000999, 12343841, 11686939, 10963729};
   private static final int[] POSSIBLE_COLORS_SOUL = new int[]{5456440, 5127475, 4009509, 4798252, 2826267};

   private ParticleGusterSandSpin(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant) {
      super(world, x, y, z);
      int color = selectColor(variant, this.random);
      float lvt_18_1_ = (color >> 16 & 0xFF) / 255.0F;
      float lvt_19_1_ = (color >> 8 & 0xFF) / 255.0F;
      float lvt_20_1_ = (color & 0xFF) / 255.0F;
      this.setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
      this.targetX = (float)motionX;
      this.targetY = (float)motionY;
      this.targetZ = (float)motionZ;
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.quadSize = this.quadSize * (0.4F + this.random.nextFloat() * 1.4F);
      this.lifetime = 15 + this.random.nextInt(15);
      this.distX = (float)(x - this.targetX);
      this.distZ = (float)(z - this.targetZ);
   }

   public static int selectColor(int variant, RandomSource rand) {
      if (variant == 2) {
         return POSSIBLE_COLORS_SOUL[rand.nextInt(POSSIBLE_COLORS_SOUL.length - 1)];
      } else {
         return variant == 1 ? POSSIBLE_COLORS_RED[rand.nextInt(POSSIBLE_COLORS_RED.length - 1)] : POSSIBLE_COLORS[rand.nextInt(POSSIBLE_COLORS.length - 1)];
      }
   }

   public void tick() {
      super.tick();
      float radius = 2.0F;
      float angle = this.age * 2;
      double extraX = this.targetX + radius * Mth.sin(3.1415927F + angle);
      double extraZ = this.targetZ + radius * Mth.cos(angle);
      double d2 = extraX - this.x;
      double d3 = this.targetY - this.y;
      double d4 = extraZ - this.z;
      float speed = 0.02F;
      this.xd += d2 * speed;
      this.yd += d3 * speed;
      this.zd += d4 * speed;
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().move(x, y, z));
      this.setLocationFromBoundingbox();
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
         ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
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
         ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1);
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
         ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 2);
         p.pickSprite(this.spriteSet);
         return p;
      }
   }
}
