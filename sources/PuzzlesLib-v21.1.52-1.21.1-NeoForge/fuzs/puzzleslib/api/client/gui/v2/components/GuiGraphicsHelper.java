package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.impl.client.gui.SingleTextureAtlasSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice.Border;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class GuiGraphicsHelper {
   private GuiGraphicsHelper() {
   }

   public static GuiGraphics create(PoseStack poseStack) {
      return create(poseStack.last().pose());
   }

   public static GuiGraphics create(Matrix4f matrix4f) {
      Minecraft minecraft = Minecraft.getInstance();
      GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
      guiGraphics.pose().mulPose(matrix4f);
      return guiGraphics;
   }

   public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int color) {
      fillFrame(guiGraphics, posX, posY, width, height, borderSize, 0, color);
   }

   public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int z, int color) {
      fillFrameArea(guiGraphics, posX, posY, posX + width, posY + height, borderSize, z, color);
   }

   public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int z, int color) {
      guiGraphics.fill(minX, minY, maxX, minY + borderSize, z, color);
      guiGraphics.fill(minX, maxY - borderSize, maxX, maxY, z, color);
      guiGraphics.fill(minX, minY + borderSize, minX + borderSize, maxY - borderSize, z, color);
      guiGraphics.fill(maxX - borderSize, minY + borderSize, maxX, maxY - borderSize, z, color);
   }

   public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int color) {
      fillFrameArea(guiGraphics, minX, minY, maxX, maxY, borderSize, 0, color);
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation resourceLocation,
      int x,
      int y,
      int width,
      int height,
      int borderSize,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      blitNineSliced(guiGraphics, resourceLocation, x, y, width, height, borderSize, borderSize, spriteWidth, spriteHeight, uOffset, vOffset);
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation resourceLocation,
      int x,
      int y,
      int width,
      int height,
      int borderWidth,
      int borderHeight,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      blitNineSliced(
         guiGraphics, resourceLocation, x, y, width, height, borderWidth, borderHeight, borderWidth, borderHeight, spriteWidth, spriteHeight, uOffset, vOffset
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation resourceLocation,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      blitNineSliced(
         guiGraphics,
         resourceLocation,
         x,
         y,
         0,
         width,
         height,
         borderLeft,
         borderTop,
         borderRight,
         borderBottom,
         spriteWidth,
         spriteHeight,
         uOffset,
         vOffset,
         256,
         256
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation resourceLocation,
      int x,
      int y,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset,
      int textureWidth,
      int textureHeight
   ) {
      blitNineSliced(
         guiGraphics,
         resourceLocation,
         x,
         y,
         0,
         width,
         height,
         borderLeft,
         borderTop,
         borderRight,
         borderBottom,
         spriteWidth,
         spriteHeight,
         uOffset,
         vOffset,
         textureWidth,
         textureHeight
      );
   }

   public static void blitNineSliced(
      GuiGraphics guiGraphics,
      ResourceLocation resourceLocation,
      int x,
      int y,
      int blitOffset,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset,
      int textureWidth,
      int textureHeight
   ) {
      SingleTextureAtlasSprite textureAtlasSprite = new SingleTextureAtlasSprite(
         resourceLocation, spriteWidth, spriteHeight, uOffset, vOffset, textureWidth, textureHeight
      );
      NineSlice nineSlice = new NineSlice(spriteWidth, spriteHeight, new Border(borderLeft, borderTop, borderRight, borderBottom));
      guiGraphics.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, blitOffset, width, height);
   }

   public static void blitNineSlicedSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int borderSize) {
      blitNineSlicedSprite(guiGraphics, sprite, x, y, 0, width, height, borderSize, borderSize, borderSize, borderSize);
   }

   public static void blitNineSlicedSprite(
      GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom
   ) {
      blitNineSlicedSprite(guiGraphics, sprite, x, y, 0, width, height, borderLeft, borderTop, borderRight, borderBottom);
   }

   public static void blitNineSlicedSprite(
      GuiGraphics guiGraphics,
      ResourceLocation sprite,
      int x,
      int y,
      int blitOffset,
      int width,
      int height,
      int borderLeft,
      int borderTop,
      int borderRight,
      int borderBottom
   ) {
      TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(sprite);
      NineSlice nineSlice = new NineSlice(
         textureAtlasSprite.contents().width(), textureAtlasSprite.contents().height(), new Border(borderLeft, borderTop, borderRight, borderBottom)
      );
      guiGraphics.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, blitOffset, width, height);
   }

   public static void blitTiledSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight) {
      blitTiledSprite(guiGraphics, sprite, x, y, 0, width, height, spriteWidth, spriteHeight);
   }

   public static void blitTiledSprite(
      GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int blitOffset, int width, int height, int spriteWidth, int spriteHeight
   ) {
      blitTiledSprite(guiGraphics, sprite, x, y, blitOffset, width, height, spriteWidth, spriteHeight, 0, 0);
   }

   public static void blitTiledSprite(
      GuiGraphics guiGraphics,
      ResourceLocation sprite,
      int x,
      int y,
      int blitOffset,
      int width,
      int height,
      int spriteWidth,
      int spriteHeight,
      int uOffset,
      int vOffset
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      TextureAtlasSprite textureatlassprite = minecraft.getGuiSprites().getSprite(sprite);
      guiGraphics.blitTiledSprite(textureatlassprite, x, y, blitOffset, width, height, uOffset, vOffset, spriteWidth, spriteHeight, spriteWidth, spriteHeight);
   }
}
