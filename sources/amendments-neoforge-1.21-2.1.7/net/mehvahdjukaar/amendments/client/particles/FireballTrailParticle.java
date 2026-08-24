package net.mehvahdjukaar.amendments.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FireballTrailParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected FireballTrailParticle(ClientLevel clientLevel, double x, double y, double z, double size, SpriteSet sprites) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = sprites;
      this.xd *= 0.1;
      this.yd *= 0.1;
      this.zd *= 0.1;
      this.quadSize = (float)size + 0.2F * clientLevel.random.nextFloat();
      this.quadSize = Math.max(0.1F, this.quadSize);
      this.lifetime = 20;
      this.setSpriteFromAge(sprites);
      this.roll = 3.1415927F * clientLevel.random.nextFloat();
      this.oRoll = this.roll;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   public int getLightColor(float partialTick) {
      int block = (int)Mth.map(this.age + partialTick, 0.0F, this.lifetime, 15.0F, 0.0F);
      int light = super.getLightColor(partialTick);
      block = Math.max(LightTexture.block(light), block);
      return LightTexture.pack(block, LightTexture.sky(light));
   }

   public float getQuadSize(float scaleFactor) {
      return this.quadSize * Mth.clamp((this.age + scaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprite) {
         this.sprites = sprite;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double size, double ySpeed, double zSpeed) {
         return new FireballTrailParticle(level, x, y, z, size, this.sprites);
      }
   }
}
