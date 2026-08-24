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
public class OwlTeleportParticle extends TextureSheetParticle {
   protected float scale;
   protected float rotationDir;
   protected float fallingSpeed;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;
   protected double ydExtra;

   public OwlTeleportParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.xdStart = motionX;
      this.ydStart = motionY;
      this.zdStart = motionZ;
      this.roll = new Random().nextFloat() * 3.1415927F;
      this.oRoll = this.roll;
      this.ydExtra = new Random().nextFloat() * (motionY / 10.0);
      this.rotationDir = new Random().nextFloat() - 0.5F;
      this.fallingSpeed = new Random().nextFloat();
      this.lifetime = 20 + (int)(new Random().nextFloat() * 20.0F);
      this.quadSize = 0.125F + 0.125F * new Random().nextFloat();
      this.setScale(0.2F);
   }

   public void setScale(float scale) {
      this.scale = scale;
      this.setSize(scale * 0.5F, scale * 0.5F);
   }

   public void tick() {
      this.xd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.xdStart;
      this.yd = Math.min(1.0F, (float)this.age / this.lifetime) * this.ydStart + this.ydExtra;
      this.zd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.zdStart;
      this.alpha = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime);
      this.oRoll = this.roll;
      if (Math.abs(this.yd) > 0.0 && this.y != this.yo) {
         this.roll = this.roll + 0.3F * this.rotationDir;
      }

      this.yd = this.yd - 0.005F * this.fallingSpeed;
      super.tick();
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
         OwlTeleportParticle extinguishParticle = new OwlTeleportParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         extinguishParticle.pickSprite(this.spriteSet);
         extinguishParticle.setColor(1.0F - colorOffset, 1.0F - colorOffset, 1.0F - colorOffset);
         return extinguishParticle;
      }
   }
}
