package net.mcreator.borninchaosv.client.particle;

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
public class LittleCarrotParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;
   private float angularVelocity;
   private float angularAcceleration;

   public static LittleCarrotParticle.LittleCarrotParticleProvider provider(SpriteSet spriteSet) {
      return new LittleCarrotParticle.LittleCarrotParticleProvider(spriteSet);
   }

   protected LittleCarrotParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(0.2F, 0.2F);
      this.quadSize *= 1.5F;
      this.lifetime = Math.max(1, 16 + (this.random.nextInt(8) - 4));
      this.gravity = 0.3F;
      this.hasPhysics = true;
      this.xd = vx * 0.1;
      this.yd = vy * 0.1;
      this.zd = vz * 0.1;
      this.angularVelocity = 0.1F;
      this.angularAcceleration = 0.03F;
      this.pickSprite(spriteSet);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      this.oRoll = this.roll;
      this.roll = this.roll + this.angularVelocity;
      this.angularVelocity = this.angularVelocity + this.angularAcceleration;
   }

   public static class LittleCarrotParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public LittleCarrotParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new LittleCarrotParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
