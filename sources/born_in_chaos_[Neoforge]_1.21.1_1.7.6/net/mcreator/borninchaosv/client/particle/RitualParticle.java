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
public class RitualParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;
   private float angularVelocity;
   private float angularAcceleration;

   public static RitualParticle.RitualParticleProvider provider(SpriteSet spriteSet) {
      return new RitualParticle.RitualParticleProvider(spriteSet);
   }

   protected RitualParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(2.0F, 2.0F);
      this.quadSize *= 1.5F;
      this.lifetime = 12;
      this.gravity = -0.3F;
      this.hasPhysics = false;
      this.xd = vx * -0.1;
      this.yd = vy * -0.1;
      this.zd = vz * -0.1;
      this.angularVelocity = 0.2F;
      this.angularAcceleration = 0.01F;
      this.setSpriteFromAge(spriteSet);
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   public void tick() {
      super.tick();
      this.oRoll = this.roll;
      this.roll = this.roll + this.angularVelocity;
      this.angularVelocity = this.angularVelocity + this.angularAcceleration;
      if (!this.removed) {
         this.setSprite(this.spriteSet.get(this.age / 2 % 7 + 1, 7));
      }
   }

   public static class RitualParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public RitualParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new RitualParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
