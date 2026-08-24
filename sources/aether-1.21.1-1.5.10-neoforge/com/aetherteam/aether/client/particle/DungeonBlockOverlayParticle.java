package com.aetherteam.aether.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class DungeonBlockOverlayParticle extends TextureSheetParticle {
   public DungeonBlockOverlayParticle(ClientLevel level, double xCoord, double yCoord, double zCoord) {
      super(level, xCoord, yCoord, zCoord);
      this.gravity = 0.0F;
      this.lifetime = 80;
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float size) {
      return 0.5F;
   }

   public record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         DungeonBlockOverlayParticle particle = new DungeonBlockOverlayParticle(level, x, y, z);
         particle.pickSprite(this.spriteSet());
         return particle;
      }
   }
}
