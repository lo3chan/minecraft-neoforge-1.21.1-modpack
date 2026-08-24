package net.mcreator.undeadrevamp.client.particle;

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
public class AcidgooParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static AcidgooParticle.AcidgooParticleProvider provider(SpriteSet spriteSet) {
      return new AcidgooParticle.AcidgooParticleProvider(spriteSet);
   }

   protected AcidgooParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(0.4F, 0.4F);
      this.quadSize *= 0.2F;
      this.lifetime = Math.max(1, 50 + (this.random.nextInt(1000) - 500));
      this.gravity = 1.0F;
      this.hasPhysics = true;
      this.xd = vx * 0.0;
      this.yd = vy * 0.0;
      this.zd = vz * 0.0;
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

   public static class AcidgooParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public AcidgooParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new AcidgooParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
