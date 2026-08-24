package net.diebuddies.physics.ocean;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class OceanSplashParticle extends TextureSheetParticle {
   private float baseAlpha;

   public OceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
      float size = Math.random() * 0.9F + 0.3F;
      this.setSize(size, size);
      this.setLifetime(Math.randomInt(7) + 5);
      this.quadSize = size;
      this.gravity = 0.981F;
      this.roll = Math.random() * 3.1415927F * 2.0F;
      this.oRoll = this.roll;
      this.xd = vx;
      this.yd = vy;
      this.zd = vz;
      float modifier = ConfigClient.oceanParticleAlpha * 0.5F;
      this.baseAlpha = Math.random() * modifier + modifier;
      this.setPos(x + this.xd, y + this.yd, z + this.zd);
   }

   public void render(VertexConsumer vertexConsumer, Camera camera, float renderPercent) {
      this.setAlpha((1.0F - org.joml.Math.min(1.0F, (this.age + renderPercent) / this.lifetime)) * this.baseAlpha);
      super.render(vertexConsumer, camera, renderPercent);
   }

   public double getX(float renderPercent) {
      return Mth.lerp(renderPercent, this.xo, this.x);
   }

   public double getY(float renderPercent) {
      return Mth.lerp(renderPercent, this.yo, this.y);
   }

   public double getZ(float renderPercent) {
      return Mth.lerp(renderPercent, this.zo, this.z);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz
      ) {
         OceanSplashParticle particle = new OceanSplashParticle(clientLevel, x, y, z, vx, vy, vz);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }
}
