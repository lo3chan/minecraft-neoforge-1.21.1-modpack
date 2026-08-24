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
public class ChaosenergyParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static ChaosenergyParticle.ChaosenergyParticleProvider provider(SpriteSet spriteSet) {
      return new ChaosenergyParticle.ChaosenergyParticleProvider(spriteSet);
   }

   protected ChaosenergyParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(2.5F, 2.5F);
      this.quadSize *= 1.3F;
      this.lifetime = 20;
      this.gravity = -0.1F;
      this.hasPhysics = false;
      this.xd = vx * 0.1;
      this.yd = vy * 0.1;
      this.zd = vz * 0.1;
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
      if (!this.removed) {
         this.setSprite(this.spriteSet.get(this.age / 2 % 11 + 1, 11));
      }
   }

   public static class ChaosenergyParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public ChaosenergyParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ChaosenergyParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
