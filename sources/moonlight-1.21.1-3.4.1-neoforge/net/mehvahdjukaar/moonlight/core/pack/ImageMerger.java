package net.mehvahdjukaar.moonlight.core.pack;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.util.List;
import net.mehvahdjukaar.moonlight.core.Moonlight;

public final class ImageMerger {
   public static NativeImage mergeSquare(List<NativeImage> images, ImageMerger.Mode mode, int backgroundColor) {
      if (images != null && !images.isEmpty()) {
         Moonlight.LOGGER.info("Merging {} images using mode {}", images.size(), mode);
         int minSide = 2147483647;
         int maxSide = -2147483648;

         for (NativeImage img : images) {
            if (img == null) {
               throw new IllegalArgumentException("null image");
            }

            int w = img.getWidth();
            int h = img.getHeight();
            if (w != h) {
               throw new IllegalArgumentException("all images must be square, got " + w + "x" + h);
            }

            minSide = Math.min(minSide, w);
            maxSide = Math.max(maxSide, w);
         }

         int n = images.size();
         int g = (int)Math.ceil(Math.sqrt(n));
         int tile = mode == ImageMerger.Mode.MIN_AREA_NO_UPSCALE ? minSide : maxSide;
         int canvas = g * tile;
         NativeImage out = new NativeImage(Format.RGBA, canvas, canvas, true);
         fillColor(out, backgroundColor);
         int rowsUsed = (n + g - 1) / g;
         int globalYPadding = (g - rowsUsed) * tile / 2;

         for (int i = 0; i < n; i++) {
            NativeImage src = images.get(i);
            int srcSize = src.getWidth();
            int row = i / g;
            int col = i % g;
            int colsThisRow = row < rowsUsed - 1 ? g : n - (rowsUsed - 1) * g;
            if (colsThisRow == 0) {
               colsThisRow = g;
            }

            int rowXPadding = (g - colsThisRow) * tile / 2;
            int cellX = rowXPadding + col * tile;
            int cellY = globalYPadding + row * tile;
            if (mode == ImageMerger.Mode.MIN_AREA_NO_UPSCALE) {
               if (srcSize == tile) {
                  blit(src, out, 0, 0, srcSize, cellX, cellY);
               } else {
                  blitScaledNearest(src, out, cellX, cellY, tile);
               }
            } else if (srcSize > tile) {
               blitScaledNearest(src, out, cellX, cellY, tile);
            } else {
               int innerPad = (tile - srcSize) / 2;
               blit(src, out, 0, 0, srcSize, cellX + innerPad, cellY + innerPad);
            }
         }

         return out;
      } else {
         throw new IllegalArgumentException("images is empty");
      }
   }

   private static void fillColor(NativeImage img, int rgba) {
      int w = img.getWidth();
      int h = img.getHeight();

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            img.setPixelRGBA(x, y, rgba);
         }
      }
   }

   private static void blit(NativeImage src, NativeImage dst, int sx, int sy, int size, int dx, int dy) {
      for (int y = 0; y < size; y++) {
         int srcY = sy + y;
         int dstY = dy + y;

         for (int x = 0; x < size; x++) {
            int rgba = src.getPixelRGBA(sx + x, srcY);
            dst.setPixelRGBA(dx + x, dstY, rgba);
         }
      }
   }

   private static void blitScaledNearest(NativeImage src, NativeImage dst, int dx, int dy, int dSize) {
      int s = src.getWidth();

      for (int y = 0; y < dSize; y++) {
         int sy = (int)((y + 0.5) * s / dSize);
         if (sy >= s) {
            sy = s - 1;
         }

         int dstY = dy + y;

         for (int x = 0; x < dSize; x++) {
            int sx = (int)((x + 0.5) * s / dSize);
            if (sx >= s) {
               sx = s - 1;
            }

            int rgba = src.getPixelRGBA(sx, sy);
            dst.setPixelRGBA(dx + x, dstY, rgba);
         }
      }
   }

   public static enum Mode {
      MIN_AREA_NO_UPSCALE,
      NO_UPSCALE_CENTER;
   }
}
