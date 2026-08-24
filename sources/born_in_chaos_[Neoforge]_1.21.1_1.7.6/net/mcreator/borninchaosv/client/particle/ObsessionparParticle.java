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
public class ObsessionparParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static ObsessionparParticle.ObsessionparParticleProvider provider(SpriteSet spriteSet) {
      return new ObsessionparParticle.ObsessionparParticleProvider(spriteSet);
   }

   protected ObsessionparParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(2.5F, 2.5F);
      this.quadSize *= 1.5F;
      this.lifetime = 24;
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
         this.setSprite(this.spriteSet.get(this.age / 3 % 9 + 1, 9));
      }
   }

   public static class ObsessionparParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public ObsessionparParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ObsessionparParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
