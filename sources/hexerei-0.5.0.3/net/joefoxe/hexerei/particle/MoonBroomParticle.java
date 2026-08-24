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
public class MoonBroomParticle extends TextureSheetParticle {
   protected float scale;
   protected float rotationDir;
   protected float fallingSpeed;

   public MoonBroomParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.roll = new Random().nextFloat() * 3.1415927F;
      this.oRoll = this.roll;
      this.rotationDir = new Random().nextFloat() - 0.5F;
      this.fallingSpeed = new Random().nextFloat();
      this.setScale(0.2F);
   }

   public void setScale(float scale) {
      this.scale = scale;
      this.setSize(scale * 0.5F, scale * 0.5F);
   }

   public void tick() {
      this.oRoll = this.roll;
      if (Math.abs(this.yd) > 0.0 && this.y != this.yo) {
         this.roll = this.roll + 0.3F * this.rotationDir;
      }

      this.yd = this.yd - 0.005F * this.fallingSpeed;
      super.tick();
   }

   public int getLightColor(float pPartialTick) {
      float time = this.level.getTimeOfDay(0.0F);
      return time > 0.25F && time < 0.75F && this.level.getMoonPhase() == 0 && !this.level.dimensionType().hasFixedTime()
         ? 15728880
         : super.getLightColor(pPartialTick);
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
         MoonBroomParticle broomParticle = new MoonBroomParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         broomParticle.pickSprite(this.spriteSet);
         broomParticle.setColor(0.6F + colorOffset, 0.6F + colorOffset, 0.6F + colorOffset);
         if (this.spriteSet.get(0, 1).atlasLocation().getPath().matches("particle/moon_brush_2")
            || this.spriteSet.get(0, 1).atlasLocation().getPath().matches("particle/moon_brush_3")
            || this.spriteSet.get(0, 1).atlasLocation().getPath().matches("particle/moon_brush_4")) {
            broomParticle.lifetime += broomParticle.lifetime * 3 + 30;
         }

         return broomParticle;
      }
   }
}
