package vazkii.psi.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class FXWisp extends TextureSheetParticle {
   private static final ParticleRenderType NORMAL_RENDER = new PsiParticleRenderType() {
      public BufferBuilder begin(@NotNull Tesselator tessellator, @NotNull TextureManager textureManager) {
         Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         AbstractTexture tex = textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES);
         tex.setFilter(true, false);
         return tessellator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public void end() {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES).restoreLastBlurMipmap();
      }

      @Override
      public String toString() {
         return "psi:wisp";
      }
   };
   private final float moteParticleScale;
   private final int moteHalfLife;

   public FXWisp(
      ClientLevel world,
      double d,
      double d1,
      double d2,
      double xSpeed,
      double ySpeed,
      double zSpeed,
      float size,
      float red,
      float green,
      float blue,
      float maxAgeMul
   ) {
      super(world, d, d1, d2, 0.0, 0.0, 0.0);
      this.xd = xSpeed;
      this.yd = ySpeed;
      this.zd = zSpeed;
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.alpha = 0.375F;
      this.gravity = 0.0F;
      this.quadSize = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * size;
      this.moteParticleScale = this.quadSize;
      this.lifetime = (int)(28.0 / (Math.random() * 0.3 + 0.7) * maxAgeMul);
      this.moteHalfLife = this.lifetime / 2;
      this.setSize(0.01F, 0.01F);
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.hasPhysics = true;
   }

   public float getQuadSize(float scaleFactor) {
      float ageScale = (float)this.age / this.moteHalfLife;
      if (ageScale > 1.0F) {
         ageScale = 2.0F - ageScale;
      }

      this.quadSize = this.moteParticleScale * ageScale * 0.5F;
      return this.quadSize;
   }

   protected int getLightColor(float partialTicks) {
      return 15728880;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return NORMAL_RENDER;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.yd = this.yd - 0.04 * this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863;
         this.yd *= 0.9800000190734863;
         this.zd *= 0.9800000190734863;
      }
   }

   public static class Factory implements ParticleProvider<WispParticleData> {
      private final SpriteSet sprite;

      public Factory(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public TextureSheetParticle createParticle(
         WispParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz
      ) {
         FXWisp ret = new FXWisp(world, x, y, z, mx, my, mz, data.size(), data.r(), data.g(), data.b(), data.maxAgeMul());
         ret.pickSprite(this.sprite);
         return ret;
      }
   }
}
