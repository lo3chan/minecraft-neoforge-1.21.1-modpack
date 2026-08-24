package org.dimdev.limlib.impl.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.limlib.impl.Limlib;

public class PostProcesser {
   private final ResourceLocation location;
   protected PostChain shader;
   private boolean loaded;

   public PostProcesser(ResourceLocation location) {
      this.location = location;
   }

   public void init(ResourceManager resourceManager) {
      try {
         this.release();
         Minecraft client = Minecraft.getInstance();
         this.shader = this.parseShader(resourceManager, client, this.location);
         this.shader.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
      } catch (IOException var3) {
         this.loaded = true;
         Limlib.LOGGER.error("Could not create screen shader {}", this.getLocation(), var3);
      }
   }

   protected PostChain parseShader(ResourceManager resourceManager, Minecraft mc, ResourceLocation location) throws IOException {
      return new PostChain(mc.getTextureManager(), resourceManager, mc.getMainRenderTarget(), location);
   }

   public void release() {
      if (this.isInitialized()) {
         try {
            assert this.shader != null;

            this.shader.close();
            this.shader = null;
         } catch (Exception var2) {
            throw new RuntimeException("Failed to release shader " + this.location, var2);
         }
      }

      this.loaded = false;
   }

   public void render(float tickDelta) {
      PostChain shader = this.getShaderEffect();
      if (shader != null) {
         RenderSystem.disableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.resetTextureMatrix();
         shader.process(tickDelta);
         Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
         RenderSystem.disableBlend();
         RenderSystem.blendFunc(770, 771);
         RenderSystem.enableDepthTest();
      }
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public boolean isInitialized() {
      return this.shader != null;
   }

   public PostChain getShaderEffect() {
      if (!this.isInitialized() && !this.isLoaded()) {
         this.init(Minecraft.getInstance().getResourceManager());
      }

      return this.shader;
   }
}
