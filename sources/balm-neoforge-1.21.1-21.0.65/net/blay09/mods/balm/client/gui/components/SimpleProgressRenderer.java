package net.blay09.mods.balm.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SimpleProgressRenderer implements ProgressRenderer {
   private final SimpleProgressRenderer.Direction direction;
   private final ResourceLocation texture;
   private final int textureWidth;
   private final int textureHeight;
   private int x;
   private int y;
   private int width;
   private int height;
   private int textureU;
   private int textureV;

   public SimpleProgressRenderer(ResourceLocation texture, int textureWidth, int textureHeight, SimpleProgressRenderer.Direction direction) {
      this.texture = texture;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.direction = direction;
   }

   public SimpleProgressRenderer pos(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public SimpleProgressRenderer size(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
   }

   public SimpleProgressRenderer uv(int u, int v) {
      this.textureU = u;
      this.textureV = v;
      return this;
   }

   public static SimpleProgressRenderer horizontal(ResourceLocation texture, int textureWidth, int textureHeight) {
      return new SimpleProgressRenderer(texture, textureWidth, textureHeight, SimpleProgressRenderer.Direction.HORIZONTAL);
   }

   public static SimpleProgressRenderer vertical(ResourceLocation texture, int textureWidth, int textureHeight) {
      return new SimpleProgressRenderer(texture, textureWidth, textureHeight, SimpleProgressRenderer.Direction.VERTICAL);
   }

   public static SimpleProgressRenderer reverseHorizontal(ResourceLocation texture, int textureWidth, int textureHeight) {
      return new SimpleProgressRenderer(texture, textureWidth, textureHeight, SimpleProgressRenderer.Direction.REVERSE_HORIZONTAL);
   }

   public static SimpleProgressRenderer reverseVertical(ResourceLocation texture, int textureWidth, int textureHeight) {
      return new SimpleProgressRenderer(texture, textureWidth, textureHeight, SimpleProgressRenderer.Direction.REVERSE_VERTICAL);
   }

   public static SimpleProgressRenderer invisible() {
      return new SimpleProgressRenderer(ResourceLocation.fromNamespaceAndPath("balm", "empty"), 0, 0, SimpleProgressRenderer.Direction.INVISIBLE);
   }

   @Override
   public void render(GuiGraphics guiGraphics, int screenX, int screenY, float progress) {
      if (!(progress <= 0.0F) && this.direction != SimpleProgressRenderer.Direction.INVISIBLE) {
         int renderX = screenX + this.x;
         int renderY = screenY + this.y;
         switch (this.direction) {
            case HORIZONTAL:
               int progressWidthx = (int)(progress * this.width);
               if (progressWidthx > 0) {
                  guiGraphics.blit(
                     this.texture, renderX, renderY, this.textureU, this.textureV, progressWidthx, this.height, this.textureWidth, this.textureHeight
                  );
               }
               break;
            case VERTICAL:
               int progressHeightx = (int)(progress * this.height);
               if (progressHeightx > 0) {
                  guiGraphics.blit(
                     this.texture, renderX, renderY, this.textureU, this.textureV, this.width, progressHeightx, this.textureWidth, this.textureHeight
                  );
               }
               break;
            case REVERSE_HORIZONTAL:
               int progressWidth = (int)(progress * this.width);
               if (progressWidth > 0) {
                  int startX = renderX + this.width - progressWidth;
                  guiGraphics.blit(
                     this.texture,
                     startX,
                     renderY,
                     this.textureU + this.width - progressWidth,
                     this.textureV,
                     progressWidth,
                     this.height,
                     this.textureWidth,
                     this.textureHeight
                  );
               }
               break;
            case REVERSE_VERTICAL:
               int progressHeight = (int)(progress * this.height);
               if (progressHeight > 0) {
                  int startY = renderY + this.height - progressHeight;
                  guiGraphics.blit(
                     this.texture,
                     renderX,
                     startY,
                     this.textureU,
                     this.textureV + this.height - progressHeight,
                     this.width,
                     progressHeight,
                     this.textureWidth,
                     this.textureHeight
                  );
               }
         }
      }
   }

   @Override
   public int getLength() {
      return switch (this.direction) {
         case HORIZONTAL, REVERSE_HORIZONTAL -> this.width;
         case VERTICAL, REVERSE_VERTICAL -> this.height;
         case INVISIBLE -> Math.max(this.width, this.height);
      };
   }

   public ResourceLocation texture() {
      return this.texture;
   }

   public int textureWidth() {
      return this.textureWidth;
   }

   public int textureHeight() {
      return this.textureHeight;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public int textureU() {
      return this.textureU;
   }

   public int textureV() {
      return this.textureV;
   }

   public SimpleProgressRenderer.Direction direction() {
      return this.direction;
   }

   public static enum Direction {
      HORIZONTAL,
      VERTICAL,
      REVERSE_HORIZONTAL,
      REVERSE_VERTICAL,
      INVISIBLE;
   }
}
