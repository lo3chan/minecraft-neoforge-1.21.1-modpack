package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import java.io.FileInputStream;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class DynamicTextureImage implements ImageRenderer {
   protected static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
   protected NativeImage image;
   protected DynamicTexture texture;
   protected final ResourceLocation uniqueLocation;
   protected final int width;
   protected final int height;
   protected final boolean textureFiltering;

   public DynamicTextureImage(NativeImage image, ResourceLocation location, boolean textureFiltering) {
      RenderSystem.assertOnRenderThread();
      this.image = image;
      this.texture = new DynamicTexture(image);
      this.texture.setFilter(textureFiltering, false);
      this.textureFiltering = textureFiltering;
      this.uniqueLocation = location;
      textureManager.register(this.uniqueLocation, this.texture);
      this.width = image.getWidth();
      this.height = image.getHeight();
   }

   @Override
   public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
      if (this.image == null) {
         return 0;
      } else {
         float ratio = (float)renderWidth / this.width;
         int targetHeight = (int)(this.height * ratio);
         GuiUtils.pushPose(graphics);
         GuiUtils.translate2D(graphics, x, y);
         GuiUtils.scale2D(graphics, ratio, ratio);
         GuiUtils.blitGuiTex(graphics, this.uniqueLocation, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height, this.textureFiltering);
         GuiUtils.popPose(graphics);
         return targetHeight;
      }
   }

   @Override
   public void close() {
      this.image.close();
      this.image = null;
      this.texture = null;
      textureManager.release(this.uniqueLocation);
   }

   public static ImageRendererFactory fromPath(Path imagePath, ResourceLocation location, boolean textureFiltering) {
      return () -> () -> new DynamicTextureImage(NativeImage.read(new FileInputStream(imagePath.toFile())), location, textureFiltering);
   }
}
