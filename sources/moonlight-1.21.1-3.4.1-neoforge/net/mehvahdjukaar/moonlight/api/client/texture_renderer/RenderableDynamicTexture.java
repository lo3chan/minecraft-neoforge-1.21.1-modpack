package net.mehvahdjukaar.moonlight.api.client.texture_renderer;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public class RenderableDynamicTexture extends DynamicTexture implements Tickable {
   @NotNull
   protected final Consumer<? super RenderableDynamicTexture> drawingFunction;
   private RenderTarget readTarget;
   private RenderTarget writeTarget;
   private final int width;
   private final int height;
   private final ResourceLocation textureLocation;
   private volatile boolean shouldTick = true;
   public boolean closed = false;

   public RenderableDynamicTexture(
      ResourceLocation resourceLocation, int width, int height, @NotNull Consumer<? extends RenderableDynamicTexture> textureDrawingFunction
   ) {
      super(width, height, false);
      RenderSystem.assertOnRenderThread();
      this.width = width;
      this.height = height;
      this.textureLocation = resourceLocation;
      this.drawingFunction = (Consumer<? super RenderableDynamicTexture>)textureDrawingFunction;
      this.setUpdateNextTick(true);
   }

   public RenderableDynamicTexture(ResourceLocation resourceLocation, int size, @NotNull Consumer<? extends RenderableDynamicTexture> textureDrawingFunction) {
      this(resourceLocation, size, size, textureDrawingFunction);
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   private static void renderCall(RenderCall call) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(call);
      } else {
         call.execute();
      }
   }

   public void redraw() {
      if (this.closed) {
         Moonlight.LOGGER.error("redraw on closed");
      } else {
         renderCall(() -> {
            this.bind();
            this.writeTarget.bindWrite(true);
            this.drawingFunction.accept(this);
            this.swapBackToFront();
            this.writeTarget.unbindWrite();
         });
      }
   }

   public void load(ResourceManager manager) {
   }

   public void swapBackToFront() {
      GlStateManager._glBindFramebuffer(36008, this.writeTarget.frameBufferId);
      GlStateManager._glBindFramebuffer(36009, this.readTarget.frameBufferId);
      GlStateManager._glBlitFrameBuffer(0, 0, this.writeTarget.width, this.writeTarget.height, 0, 0, this.readTarget.width, this.readTarget.height, 16384, 9728);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public RenderTarget getRenderTarget() {
      return this.writeTarget;
   }

   public void bind() {
      if (this.closed) {
         Moonlight.LOGGER.error("bind on closed");
      } else {
         super.bind();
      }
   }

   public int getId() {
      if (this.closed) {
         Moonlight.LOGGER.error("get id on closed");
         return 0;
      } else {
         RenderSystem.assertOnRenderThreadOrInit();
         if (this.readTarget == null || this.writeTarget == null) {
            int w = this.getPixels().getWidth();
            int h = this.getPixels().getHeight();
            this.readTarget = new TextureTarget(w, h, false, Minecraft.ON_OSX);
            this.writeTarget = new TextureTarget(w, h, true, Minecraft.ON_OSX);
         }

         return this.readTarget.getColorTextureId();
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void releaseId() {
      this.closed = true;
      super.releaseId();
      renderCall(() -> {
         if (this.writeTarget != null) {
            this.writeTarget.destroyBuffers();
            this.writeTarget = null;
         }

         if (this.readTarget != null) {
            this.readTarget.destroyBuffers();
            this.readTarget = null;
         }
      });
   }

   public void download() {
      if (this.closed) {
         Moonlight.LOGGER.error("download id on closed");
      } else {
         this.bindBackBuffer();
         this.getPixels().downloadTexture(0, false);
      }
   }

   public void upload() {
      if (this.closed) {
         Moonlight.LOGGER.error("upload on closed");
      } else {
         this.bindBackBuffer();
         this.getPixels().upload(0, 0, 0, false);
      }
   }

   private void bindBackBuffer() {
      this.getId();
      RenderSystem.bindTexture(this.writeTarget.getColorTextureId());
   }

   public void setUpdateNextTick(boolean shouldTick) {
      this.shouldTick = shouldTick;
   }

   @Internal
   public void tick() {
      if (this.shouldTick) {
         this.shouldTick = false;
         this.redraw();
      }
   }

   public void register() {
      Minecraft.getInstance().getTextureManager().register(this.textureLocation, this);
   }

   public void unregister() {
      TextureManager tm = Minecraft.getInstance().getTextureManager();
      AbstractTexture t = tm.getTexture(this.textureLocation);
      if (t == this) {
         tm.release(this.textureLocation);
      }
   }

   public boolean isClosed() {
      return this.closed;
   }
}
