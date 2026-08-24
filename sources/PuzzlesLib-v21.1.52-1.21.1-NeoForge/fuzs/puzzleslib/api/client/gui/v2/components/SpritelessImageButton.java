package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.ToIntFunction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public class SpritelessImageButton extends Button {
   public static final ToIntFunction<Button> TEXTURE_LAYOUT = button -> !button.isActive() ? 2 : (button.isHoveredOrFocused() ? 1 : 0);
   public static final ToIntFunction<Button> LEGACY_TEXTURE_LAYOUT = button -> !button.isActive() ? 0 : (button.isHoveredOrFocused() ? 2 : 1);
   public static final ToIntFunction<Button> SINGLE_TEXTURE_LAYOUT = button -> 0;
   public ResourceLocation resourceLocation;
   public int xTexStart;
   public int yTexStart;
   public int yDiffTex;
   public int textureWidth;
   public int textureHeight;
   private ToIntFunction<Button> textureLayout = TEXTURE_LAYOUT;
   private boolean drawBackground;

   public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, OnPress onPress) {
      this(x, y, width, height, xTexStart, yTexStart, height, resourceLocation, 256, 256, onPress);
   }

   public SpritelessImageButton(
      int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress
   ) {
      this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, 256, 256, onPress);
   }

   public SpritelessImageButton(
      int x,
      int y,
      int width,
      int height,
      int xTexStart,
      int yTexStart,
      int yDiffTex,
      ResourceLocation resourceLocation,
      int textureWidth,
      int textureHeight,
      OnPress onPress
   ) {
      this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, CommonComponents.EMPTY);
   }

   public SpritelessImageButton(
      int x,
      int y,
      int width,
      int height,
      int xTexStart,
      int yTexStart,
      int yDiffTex,
      ResourceLocation resourceLocation,
      int textureWidth,
      int textureHeight,
      OnPress onPress,
      Component message
   ) {
      super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.xTexStart = xTexStart;
      this.yTexStart = yTexStart;
      this.yDiffTex = yDiffTex;
      this.resourceLocation = resourceLocation;
   }

   public SpritelessImageButton setTextureCoordinates(int xTexStart, int yTexStart) {
      this.xTexStart = xTexStart;
      this.yTexStart = yTexStart;
      return this;
   }

   public SpritelessImageButton setTextureDimensions(int textureWidth, int textureHeight) {
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      return this;
   }

   public SpritelessImageButton setTextureLayout(ToIntFunction<Button> textureLayout) {
      this.textureLayout = textureLayout;
      return this;
   }

   public SpritelessImageButton setDrawBackground() {
      this.drawBackground = true;
      return this;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.drawBackground) {
         super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
      } else {
         guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
      }

      guiGraphics.blit(
         this.resourceLocation,
         this.getX(),
         this.getY(),
         this.xTexStart,
         this.yTexStart + this.yDiffTex * this.getTextureY(),
         this.width,
         this.height,
         this.textureWidth,
         this.textureHeight
      );
   }

   public void renderString(GuiGraphics guiGraphics, Font font, int color) {
   }

   protected int getTextureY() {
      return this.textureLayout.applyAsInt(this);
   }
}
