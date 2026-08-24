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
public class FXSparkle extends TextureSheetParticle {
   private static final ParticleRenderType NORMAL_RENDER = new PsiParticleRenderType() {
      public BufferBuilder begin(@NotNull Tesselator tessellator, @NotNull TextureManager textureManager) {
         Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
         RenderSystem.enableDepthTest();
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
         return "psi:sparkle";
      }
   };

   public FXSparkle(
      ClientLevel world, double x, double y, double z, float size, float red, float green, float blue, int m, double mx, double my, double mz, SpriteSet sprite
   ) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.alpha = 0.5F;
      this.gravity = 0.0F;
      this.xd = mx;
      this.yd = my;
      this.zd = mz;
      this.quadSize *= size;
      this.lifetime = 3 * m;
      this.setSize(0.01F, 0.01F);
      this.setBoundingBox(this.getBoundingBox().inflate(mx * 10.0, my * 10.0, mz * 10.0));
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.setSpriteFromAge(sprite);
   }

   public float getQuadSize(float partialTicks) {
      return this.quadSize * (this.lifetime - this.age + 1) / this.lifetime;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.x = this.x + this.xd;
         this.y = this.y + this.yd;
         this.z = this.z + this.zd;
         this.xd *= 0.8999999761581421;
         this.yd *= 0.8999999761581421;
         this.zd *= 0.8999999761581421;
         if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
         }
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return NORMAL_RENDER;
   }

   public static class Factory implements ParticleProvider<SparkleParticleData> {
      private final SpriteSet sprite;

      public Factory(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public TextureSheetParticle createParticle(
         SparkleParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz
      ) {
         return new FXSparkle(world, x, y, z, data.size(), data.r(), data.g(), data.b(), data.m(), mx, my, mz, this.sprite);
      }
   }
}
