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
public class ZeesleepParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static ZeesleepParticle.ZeesleepParticleProvider provider(SpriteSet spriteSet) {
      return new ZeesleepParticle.ZeesleepParticleProvider(spriteSet);
   }

   protected ZeesleepParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.setSize(0.5F, 0.5F);
      this.quadSize *= 3.0F;
      this.lifetime = Math.max(1, 34 + (this.random.nextInt(70) - 35));
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.xd = vx * 0.2;
      this.yd = vy * 0.2;
      this.zd = vz * 0.2;
      this.setSpriteFromAge(spriteSet);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSprite(this.spriteSet.get(this.age / 5 % 7 + 1, 7));
      }
   }

   public static class ZeesleepParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public ZeesleepParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ZeesleepParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
