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
public class PumpkinStaffSParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static PumpkinStaffSParticle.PumpkinStaffSParticleProvider provider(SpriteSet spriteSet) {
      return new PumpkinStaffSParticle.PumpkinStaffSParticleProvider(spriteSet);
   }

   protected PumpkinStaffSParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(1.5F, 1.5F);
      this.lifetime = Math.max(1, 10 + (this.random.nextInt(10) - 5));
      this.gravity = -0.1F;
      this.hasPhysics = true;
      this.xd = vx * 0.2;
      this.yd = vy * 0.2;
      this.zd = vz * 0.2;
      this.pickSprite(spriteSet);
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   public void tick() {
      super.tick();
   }

   public static class PumpkinStaffSParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public PumpkinStaffSParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new PumpkinStaffSParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
