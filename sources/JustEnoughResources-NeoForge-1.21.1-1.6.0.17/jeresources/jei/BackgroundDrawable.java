package jeresources.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import jeresources.util.RenderHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BackgroundDrawable implements IDrawable {
   private final int width;
   private final int height;
   private final ResourceLocation resource;
   private static final int PADDING = 5;

   public BackgroundDrawable(String resource, int width, int height) {
      this.resource = ResourceLocation.fromNamespaceAndPath("jeresources", resource);
      this.width = width;
      this.height = height;
   }

   public int getWidth() {
      return this.width + 10;
   }

   public int getHeight() {
      return this.height + 10;
   }

   public void draw(@NotNull GuiGraphics guiGraphics, int xOffset, int yOffset) {
      RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, this.resource);
      RenderHelper.drawTexturedModalRect(guiGraphics, xOffset + 5, yOffset + 5, 0, 0, this.width, this.height, 0.0F);
      RenderSystem.applyModelViewMatrix();
   }

   public ResourceLocation getResource() {
      return this.resource;
   }
}
