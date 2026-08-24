package net.mehvahdjukaar.moonlight.api.client.texture_renderer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class DynamicTextureRenderer {
   private static final LoadingCache<ResourceLocation, CompletableFuture<RenderableDynamicTexture>> TEXTURE_CACHE = CacheBuilder.newBuilder()
      .removalListener(i -> {
         CompletableFuture<RenderableDynamicTexture> future = (CompletableFuture<RenderableDynamicTexture>)i.getValue();
         if (future != null) {
            future.thenAccept(texture -> RenderSystem.recordRenderCall(texture::unregister));
         }
      })
      .expireAfterAccess(2L, TimeUnit.MINUTES)
      .build(new CacheLoader<ResourceLocation, CompletableFuture<RenderableDynamicTexture>>() {
         public CompletableFuture<RenderableDynamicTexture> load(ResourceLocation key) {
            return new CompletableFuture<>();
         }
      });

   public static void clearCache() {
      TEXTURE_CACHE.invalidateAll();
   }

   public static <T extends RenderableDynamicTexture> T requestTexture(ResourceLocation id, Supplier<T> textureSupplier) {
      CompletableFuture<RenderableDynamicTexture> future = TEXTURE_CACHE.asMap().computeIfAbsent(id, key -> {
         CompletableFuture<RenderableDynamicTexture> f = new CompletableFuture<>();
         RenderSystem.recordRenderCall(() -> {
            try {
               T newText = textureSupplier.get();
               newText.register();
               newText.redraw();
               f.complete(newText);
            } catch (Throwable var4) {
               Moonlight.LOGGER.error("Failed to create dynamic texture for id {}", key, var4);
               f.completeExceptionally(var4);
               TEXTURE_CACHE.invalidate(key);
            }
         });
         return f;
      });
      if (!future.isDone()) {
         return null;
      } else {
         T t = (T)future.join();
         if (t.isClosed()) {
            TEXTURE_CACHE.invalidate(id);
            Moonlight.LOGGER.error("get texture on closed");
            return null;
         } else {
            return t;
         }
      }
   }

   public static RenderableDynamicTexture requestTexture(
      ResourceLocation id, int textureSize, @NotNull Consumer<RenderableDynamicTexture> textureDrawingFunction, boolean updateEachFrame
   ) {
      RenderableDynamicTexture t = requestTexture(id, () -> new RenderableDynamicTexture(id, textureSize, textureDrawingFunction));
      if (t != null && updateEachFrame) {
         t.setUpdateNextTick(true);
      }

      return t;
   }

   @Nullable
   public static <T extends RenderableDynamicTexture> T getTextureIfPresent(ResourceLocation id) {
      CompletableFuture<RenderableDynamicTexture> ifPresent = (CompletableFuture<RenderableDynamicTexture>)TEXTURE_CACHE.getIfPresent(id);
      return (T)(ifPresent != null && ifPresent.isDone() ? ifPresent.join() : null);
   }

   public static RenderableDynamicTexture requestFlatItemStackTexture(ResourceLocation res, ItemStack stack, int size) {
      return requestTexture(res, size, t -> drawItem(t, stack), true);
   }

   public static RenderableDynamicTexture requestFlatItemTexture(Item item, int size) {
      return requestFlatItemTexture(item, size, null);
   }

   public static RenderableDynamicTexture requestFlatItemTexture(Item item, int size, @Nullable Consumer<NativeImage> postProcessing) {
      ResourceLocation id = Moonlight.res(Utils.getID(item).toString().replace(":", "/") + "/" + size);
      return requestFlatItemTexture(id, item, size, postProcessing, false);
   }

   public static RenderableDynamicTexture requestFlatItemTexture(ResourceLocation id, Item item, int size, @Nullable Consumer<NativeImage> postProcessing) {
      return requestFlatItemTexture(id, item, size, postProcessing, false);
   }

   public static RenderableDynamicTexture requestFlatItemTexture(
      ResourceLocation id, Item item, int size, @Nullable Consumer<NativeImage> postProcessing, boolean updateEachFrame
   ) {
      return requestTexture(id, size, t -> {
         drawItem(t, item.getDefaultInstance());
         if (postProcessing != null) {
            t.download();
            NativeImage img = t.getPixels();
            postProcessing.accept(img);
            t.upload();
         }
      }, updateEachFrame);
   }

   public static void drawItem(RenderableDynamicTexture tex, ItemStack stack) {
      drawAsInGUI(tex, g -> g.renderFakeItem(stack, 0, 0));
   }

   public static void drawTexture(RenderableDynamicTexture tex, ResourceLocation texture) {
      drawAsInGUI(tex, s -> {
         RenderSystem.setShaderTexture(0, texture);
         Pose pose = s.pose().last();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableBlend();
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(pose, 0.0F, 16.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(pose, 16.0F, 16.0F, 0.0F).setUv(1.0F, 0.0F);
         bufferBuilder.addVertex(pose, 16.0F, 0.0F, 0.0F).setUv(1.0F, 1.0F);
         bufferBuilder.addVertex(pose, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F);
         BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
      });
   }

   public static void drawNormalized(RenderableDynamicTexture tex, Consumer<PoseStack> drawFunction) {
      drawAsInGUI(tex, g -> {
         PoseStack s = g.pose();
         float scale = 0.0625F;
         s.translate(8.0F, 8.0F, 0.0F);
         s.scale(scale, scale, 1.0F);
         drawFunction.accept(s);
      });
   }

   public static void drawAsInGUI(RenderableDynamicTexture tex, Consumer<GuiGraphics> drawFunction) {
      float fogStart = RenderSystem.getShaderFogStart();
      float fogEnd = RenderSystem.getShaderFogEnd();
      RenderSystem.setShaderFogStart(2.1474836E9F);
      RenderSystem.setShaderFogEnd(2.1474836E9F);
      RenderSystem.clear(256, Minecraft.ON_OSX);
      Minecraft mc = Minecraft.getInstance();
      RenderTarget frameBuffer = tex.getRenderTarget();
      frameBuffer.clear(Minecraft.ON_OSX);
      frameBuffer.bindWrite(true);
      int size = 16;
      RenderSystem.backupProjectionMatrix();
      Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, size, size, 0.0F, -1000.0F, 1000.0F);
      RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
      Matrix4fStack posestack = RenderSystem.getModelViewStack();
      posestack.pushMatrix();
      posestack.set(new Matrix4f().identity());
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
      GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
      drawFunction.accept(guiGraphics);
      guiGraphics.flush();
      posestack.popMatrix();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.restoreProjectionMatrix();
      mc.getMainRenderTarget().bindWrite(true);
      RenderSystem.setShaderFogStart(fogStart);
      RenderSystem.setShaderFogEnd(fogEnd);
   }
}
