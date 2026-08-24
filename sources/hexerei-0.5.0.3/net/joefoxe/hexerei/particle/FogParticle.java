package net.joefoxe.hexerei.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FogParticle extends TextureSheetParticle {
   private static final ParticleRenderType renderType = new ParticleRenderType() {
      @Nullable
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableCull();
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }
   };

   public FogParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.lifetime = 200;
      this.quadSize = 2.0F;
   }

   public void tick() {
      this.alpha = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime);
      super.tick();
   }

   public ParticleRenderType getRenderType() {
      return renderType;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random rand = new Random();
         float colorOffset = rand.nextFloat() * 0.4F;
         FogParticle fogParticle = new FogParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         fogParticle.pickSprite(this.spriteSet);
         fogParticle.setColor(0.6F + colorOffset, 0.6F + colorOffset, 0.6F + colorOffset);
         return fogParticle;
      }
   }
}
