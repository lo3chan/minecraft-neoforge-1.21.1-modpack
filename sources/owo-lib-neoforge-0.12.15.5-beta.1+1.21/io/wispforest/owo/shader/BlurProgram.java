package io.wispforest.owo.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL30;

public class BlurProgram extends GlProgram {
   private Uniform inputResolution;
   private Uniform directions;
   private Uniform quality;
   private Uniform size;
   private RenderTarget input;

   public BlurProgram() {
      super(ResourceLocation.fromNamespaceAndPath("owo", "blur"), DefaultVertexFormat.POSITION);
      WindowResizeCallback.EVENT.register((WindowResizeCallback)(client, window) -> {
         if (this.input != null) {
            this.input.resize(window.getWidth(), window.getHeight(), Minecraft.ON_OSX);
         }
      });
   }

   public void setParameters(int directions, float quality, float size) {
      this.directions.set(directions);
      this.size.set(size);
      this.quality.set(quality);
   }

   @Override
   public void use() {
      RenderTarget buffer = Minecraft.getInstance().getMainRenderTarget();
      this.input.bindWrite(false);
      GL30.glBindFramebuffer(36008, buffer.frameBufferId);
      GL30.glBlitFramebuffer(0, 0, buffer.width, buffer.height, 0, 0, buffer.width, buffer.height, 16384, 9729);
      buffer.bindWrite(false);
      this.inputResolution.set(buffer.width, buffer.height);
      this.backingProgram.setSampler("InputSampler", this.input.getColorTextureId());
      super.use();
   }

   @Override
   protected void setup() {
      this.inputResolution = this.findUniform("InputResolution");
      this.directions = this.findUniform("Directions");
      this.quality = this.findUniform("Quality");
      this.size = this.findUniform("Size");
      Window window = Minecraft.getInstance().getWindow();
      this.input = new TextureTarget(window.getWidth(), window.getHeight(), false, Minecraft.ON_OSX);
   }
}
