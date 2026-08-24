package mezz.jei.common.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.common.gui.textures.JeiGuiSpriteManager;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Tile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.joml.Matrix4f;

public class ScalableDrawable implements IScalableDrawable {
   private final Supplier<TextureAtlasSprite> spriteSupplier;
   private final Function<TextureAtlasSprite, GuiSpriteScaling> scalingSupplier;

   public ScalableDrawable(JeiGuiSpriteManager spriteManager, ResourceLocation spriteId) {
      this(() -> spriteManager.getSprite(spriteId), spriteManager::getSpriteScaling);
   }

   public ScalableDrawable(TextureAtlas textureAtlas, ResourceLocation spriteId) {
      this(() -> textureAtlas.getSprite(spriteId), ScalableDrawable::getSpriteScaling);
   }

   private ScalableDrawable(Supplier<TextureAtlasSprite> spriteSupplier, Function<TextureAtlasSprite, GuiSpriteScaling> scalingSupplier) {
      this.spriteSupplier = spriteSupplier;
      this.scalingSupplier = scalingSupplier;
   }

   public void draw(GuiGraphics guiGraphics, ImmutableRect2i area) {
      this.draw(guiGraphics, area.getX(), area.getY(), area.getWidth(), area.getHeight());
   }

   @Override
   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset, int width, int height) {
      TextureAtlasSprite sprite = this.spriteSupplier.get();
      GuiSpriteScaling scaling = this.scalingSupplier.apply(sprite);
      switch (scaling) {
         case Tile tileScaling:
            blitTiledSpriteWithColor(guiGraphics, sprite, tileScaling, xOffset, yOffset, width, height, -1);
            break;
         case NineSlice nineSliceScaling: {
            IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
            renderHelper.blitNineSlicedSprite(guiGraphics, sprite, nineSliceScaling, xOffset, yOffset, width, height);
            break;
         }
         default: {
            IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
            renderHelper.blitSprite(guiGraphics, sprite, width, height, 0, 0, xOffset, yOffset, width, height);
         }
      }
   }

   public static void blitTiledSpriteWithColor(
      GuiGraphics guiGraphics, TextureAtlasSprite sprite, Tile scaling, int xOffset, int yOffset, int width, int height, int color
   ) {
      int tileWidth = scaling.width();
      int tileHeight = scaling.height();
      if (width > 0 && height > 0) {
         if (tileWidth > 0 && tileHeight > 0) {
            for (int xTile = 0; xTile < width; xTile += tileWidth) {
               int uWidth = Math.min(tileWidth, width - xTile);

               for (int yTile = 0; yTile < height; yTile += tileHeight) {
                  int vHeight = Math.min(tileHeight, height - yTile);
                  blitSprite(guiGraphics, sprite, tileWidth, tileHeight, 0, 0, xOffset + xTile, yOffset + yTile, uWidth, vHeight, color);
               }
            }
         } else {
            throw new IllegalArgumentException("Tile size must be positive, got " + tileWidth + "x" + tileHeight);
         }
      }
   }

   private static void blitSprite(
      GuiGraphics guiGraphics,
      TextureAtlasSprite sprite,
      int textureWidth,
      int textureHeight,
      int uPosition,
      int vPosition,
      int x,
      int y,
      int uWidth,
      int vHeight,
      int color
   ) {
      if (uWidth > 0 && vHeight > 0) {
         float u0 = sprite.getU((float)uPosition / textureWidth);
         float u1 = sprite.getU((float)(uPosition + uWidth) / textureWidth);
         float v0 = sprite.getV((float)vPosition / textureHeight);
         float v1 = sprite.getV((float)(vPosition + vHeight) / textureHeight);
         float alpha = (color >> 24 & 0xFF) / 255.0F;
         float red = (color >> 16 & 0xFF) / 255.0F;
         float green = (color >> 8 & 0xFF) / 255.0F;
         float blue = (color & 0xFF) / 255.0F;
         RenderSystem.setShaderTexture(0, sprite.atlasLocation());
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.enableBlend();
         Matrix4f matrix = guiGraphics.pose().last().pose();
         BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bufferBuilder.addVertex(matrix, x, y, 0.0F).setUv(u0, v0).setColor(red, green, blue, alpha);
         bufferBuilder.addVertex(matrix, x, y + vHeight, 0.0F).setUv(u0, v1).setColor(red, green, blue, alpha);
         bufferBuilder.addVertex(matrix, x + uWidth, y + vHeight, 0.0F).setUv(u1, v1).setColor(red, green, blue, alpha);
         bufferBuilder.addVertex(matrix, x + uWidth, y, 0.0F).setUv(u1, v0).setColor(red, green, blue, alpha);
         BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
         RenderSystem.disableBlend();
      }
   }

   private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
      SpriteContents contents = sprite.contents();
      ResourceMetadata metadata = contents.metadata();
      return metadata.getSection(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT).scaling();
   }
}
