package dev.latvian.mods.kubejs.client.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public interface KubeIconRenderer {
   Lazy<Map<KubeIconType<?>, Function<?, KubeIconRenderer>>> RENDERERS = Lazy.map(map -> {
      KubeIconRenderer.Registry registry = map::put;
      registry.register(TextureKubeIcon.TYPE, KubeIconRenderer.FromTexture::new);
      registry.register(AtlasSpriteKubeIcon.TYPE, KubeIconRenderer.FromAtlasSprite::new);
      registry.register(ItemKubeIcon.TYPE, KubeIconRenderer.FromItem::new);
   });

   @Nullable
   static KubeIconRenderer from(KubeIcon icon) {
      Function<?, KubeIconRenderer> factory = RENDERERS.get().get(icon.getType());
      return factory != null ? ((Function<Object, KubeIconRenderer>)factory).apply(Cast.to(icon)) : null;
   }

   void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size);

   public record FromAtlasSprite(AtlasSpriteKubeIcon icon) implements KubeIconRenderer {
      @Override
      public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
         TextureAtlasSprite sprite = (TextureAtlasSprite)(this.icon.atlas().isEmpty()
               ? mc.kjs$getBlockTextureAtlas()
               : mc.getTextureAtlas(this.icon.atlas().get()))
            .apply(this.icon.sprite());
         RenderSystem.setShaderTexture(0, sprite.atlasLocation());
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Matrix4f m = graphics.pose().last().pose();
         int p0 = -size / 2;
         int p1 = p0 + size;
         float u0 = sprite.getU0();
         float v0 = sprite.getV0();
         float u1 = sprite.getU1();
         float v1 = sprite.getV1();
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         buf.addVertex(m, x + p0, y + p1, 0.0F).setUv(u0, v1).setColor(255, 255, 255, 255);
         buf.addVertex(m, x + p1, y + p1, 0.0F).setUv(u1, v1).setColor(255, 255, 255, 255);
         buf.addVertex(m, x + p1, y + p0, 0.0F).setUv(u1, v0).setColor(255, 255, 255, 255);
         buf.addVertex(m, x + p0, y + p0, 0.0F).setUv(u0, v0).setColor(255, 255, 255, 255);
         BufferUploader.drawWithShader(buf.buildOrThrow());
      }
   }

   public record FromItem(ItemKubeIcon icon) implements KubeIconRenderer {
      @Override
      public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
         Matrix4fStack m = RenderSystem.getModelViewStack();
         m.pushMatrix();
         m.translate(x - 2.0F, y + 2.0F, 0.0F);
         float s = size / 16.0F;
         m.scale(s, s, s);
         RenderSystem.applyModelViewMatrix();
         graphics.renderFakeItem(this.icon.item(), -8, -8);
         m.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   public record FromTexture(TextureKubeIcon icon) implements KubeIconRenderer {
      @Override
      public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
         RenderSystem.setShaderTexture(0, this.icon.texture());
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Matrix4f m = graphics.pose().last().pose();
         int p0 = -size / 2;
         int p1 = p0 + size;
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         buf.addVertex(m, x + p0, y + p1, 0.0F).setUv(0.0F, 1.0F).setColor(255, 255, 255, 255);
         buf.addVertex(m, x + p1, y + p1, 0.0F).setUv(1.0F, 1.0F).setColor(255, 255, 255, 255);
         buf.addVertex(m, x + p1, y + p0, 0.0F).setUv(1.0F, 0.0F).setColor(255, 255, 255, 255);
         buf.addVertex(m, x + p0, y + p0, 0.0F).setUv(0.0F, 0.0F).setColor(255, 255, 255, 255);
         BufferUploader.drawWithShader(buf.buildOrThrow());
      }
   }

   public interface Registry {
      <T extends KubeIcon> void register(KubeIconType<T> type, Function<T, KubeIconRenderer> factory);
   }
}
