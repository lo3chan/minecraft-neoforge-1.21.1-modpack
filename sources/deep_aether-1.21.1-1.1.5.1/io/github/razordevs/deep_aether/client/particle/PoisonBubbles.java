package io.github.razordevs.deep_aether.client.particle;

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
public class PoisonBubbles extends TextureSheetParticle {
   protected PoisonBubbles(ClientLevel clientLevel, double v, double v1, double v2, double v3, double v4, double v5) {
      super(clientLevel, v, v1, v2);
      this.gravity = -0.1F;
      this.friction = 0.85F;
      this.setSize(0.02F, 0.02F);
      this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
      this.xd = v3 * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
      this.yd = v4 * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
      this.zd = v5 * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
      this.lifetime = (int)(10.0 / (Math.random() * 0.8 + 0.2));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.setSpriteFromAge(PoisonBubbles.Provider.sprite);
      super.tick();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      protected static SpriteSet sprite = null;

      public Provider(SpriteSet spriteSet) {
         sprite = spriteSet;
      }

      public Particle createParticle(SimpleParticleType particleType, ClientLevel clientLevel, double v, double v1, double v2, double v3, double v4, double v5) {
         PoisonBubbles poisonBubbles = new PoisonBubbles(clientLevel, v, v1, v2, v3, v4, v5);
         poisonBubbles.setSpriteFromAge(sprite);
         return poisonBubbles;
      }
   }
}
