package net.mehvahdjukaar.moonlight.api.resources.textures;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.math.colors.RGBColor;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.McMetaFile;
import net.minecraft.util.FastColor.ABGR32;
import net.minecraft.world.level.block.Rotation;

public final class TextureOps {
   public static void grayscale(TextureImage img) {
      img.forEachPixel(pixel -> pixel.setValue(new RGBColor(pixel.getValue()).asHCL().withChroma(0.0F).asRGB().toInt()));
   }

   public static void applyOverlay(TextureImage img, TextureImage... overlays) {
      applyOverlay(img, false, overlays);
   }

   public static void applyOverlayOnExisting(TextureImage img, TextureImage... overlays) {
      applyOverlay(img, true, overlays);
   }

   private static void applyOverlay(TextureImage baseImage, boolean onlyOnExisting, TextureImage... overlays) {
      int baseFrameWidth = baseImage.frameWidth();
      int baseFrameHeight = baseImage.frameHeight();

      for (TextureImage overlay : overlays) {
         if (overlay.frameWidth() < baseFrameWidth) {
            throw new IllegalStateException("Overlay width too small (overlay W: " + overlay.frameWidth() + ", base W: " + baseFrameWidth + ")");
         }

         if (overlay.frameHeight() < baseFrameHeight) {
            throw new IllegalStateException("Overlay height too small (overlay H: " + overlay.frameHeight() + ", base H: " + baseFrameHeight + ")");
         }
      }

      for (TextureImage overlay : overlays) {
         baseImage.forEachPixel(pixel -> {
            int frameX = pixel.frameX();
            int frameY = pixel.frameY();
            int overlayFrame = Math.min(pixel.frameIndex(), overlay.frameCount() - 1);
            int overlayPixel = overlay.getFramePixel(overlayFrame, frameX, frameY);
            if (!onlyOnExisting || ABGR32.alpha(overlayPixel) != 0) {
               pixel.blendValue(overlayPixel);
            }
         });
      }
   }

   public static void makeOpaque(TextureImage img, int backgroundColor) {
      img.forEachPixel(pixel -> {
         int oldValue = pixel.getValue();
         int alpha = ABGR32.alpha(oldValue);
         if (alpha == 0) {
            pixel.setValue(backgroundColor);
         } else {
            int newColor = ABGR32.color(255, ABGR32.red(oldValue), ABGR32.green(oldValue), ABGR32.blue(oldValue));
            pixel.setValue(newColor);
         }
      });
   }

   private static void applyMask(TextureImage img, TextureImage mask, boolean discardOpaque) {
      if (mask.frameWidth() >= img.frameWidth() && mask.frameHeight() >= img.frameHeight()) {
         int maskFrames = mask.frameCount();
         img.forEachPixel(pixel -> {
            int maskPixel = mask.getFramePixel(pixel.frameIndex() % maskFrames, pixel.frameX(), pixel.frameY());
            boolean maskOpaque = ABGR32.alpha(maskPixel) != 0;
            if (maskOpaque == discardOpaque) {
               pixel.setValue(0);
            }
         });
      } else {
         Moonlight.LOGGER
            .error(
               "applyMask - Palette mask {} needs to be at least as large as the target image {}. You must alter the mask's frame size {}x{} to match the texture's frame size {}x{}",
               mask.debugPath,
               img.debugPath,
               mask.frameWidth(),
               mask.frameHeight(),
               img.frameWidth(),
               img.frameHeight()
            );
         if (PlatHelper.isDev()) {
            throw new IllegalArgumentException("Palette mask " + mask.debugPath + " has invalid frame size");
         }
      }
   }

   public static void applyMask(TextureImage img, TextureImage mask) {
      applyMask(img, mask, true);
   }

   public static void applyMaskInverted(TextureImage img, TextureImage mask) {
      applyMask(img, mask, false);
   }

   public static void tileTexture(TextureImage image, TextureImage toTileOn, int xOff, int yOff) {
      int tileW = toTileOn.imageWidth();
      int tileH = toTileOn.imageHeight();
      image.forEachPixel(pixel -> {
         int x = pixel.x();
         int y = pixel.y();
         int srcX = Math.floorMod(x - xOff, tileW);
         int srcY = Math.floorMod(y - yOff, tileH);
         int val = toTileOn.getPixel(srcX, srcY);
         pixel.setValue(val);
      });
   }

   public static TextureImage createSingleFrameAnimation(TextureImage img, int length, McMetaFile animationData) {
      if (length <= 0) {
         throw new IllegalArgumentException("Length must be greater than 0");
      } else {
         McMetaFile newMetadata = animationData.cloneWithSize(img.frameWidth(), img.frameHeight());
         if (length == 1) {
            return img.makeCopyWithMetadata(newMetadata);
         } else {
            TextureImage newImage = TextureImage.createNew(img.frameWidth(), img.frameHeight() * length, newMetadata);
            newImage.forEachPixel(pixel -> {
               int xo = pixel.localX;
               int yo = pixel.localY;
               pixel.setValue(img.getFramePixel(0, xo, yo));
            });
            return newImage;
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static TextureImage createSingleFrameAnimation(TextureImage img, McMetaFile animationData) {
      return createSingleFrameAnimation(img, img.frameCount(), animationData);
   }

   public static TextureImage createScaled(TextureImage img, float widthScale, float heightScale) {
      int newW = (int)(img.imageWidth() * widthScale);
      int newH = (int)(img.imageHeight() * heightScale);
      McMetaFile meta = null;
      McMetaFile metadata = img.getMcMeta();
      if (metadata != null) {
         meta = metadata.hasAnimation()
            ? metadata.cloneWithSize((int)(metadata.getAnimationFrameWidth() * widthScale), (int)(metadata.getAnimationFrameHeight() * heightScale))
            : metadata.copy();
      }

      TextureImage im = TextureImage.createNew(newW, newH, meta);
      TextureCollager transformer = TextureCollager.builder(img.frameWidth(), img.frameHeight(), im.frameWidth(), im.frameHeight())
         .copyFrom(0, 0, img.frameWidth(), img.frameHeight())
         .to(0, 0, im.frameWidth(), im.frameHeight())
         .build();
      transformer.apply(img, im);
      return im;
   }

   public static TextureImage createRotated(TextureImage img, Rotation rotation) {
      TextureImage flippedImage = TextureImage.createNew(img.frameHeight(), img.frameWidth() * img.frameCount(), img.getMcMeta());
      img.forEachPixel(context -> {
         int frameX = context.frameX();
         int frameY = context.frameY();
         int frameIndex = context.frameIndex();
         int newFrameX = frameX;
         int newFrameY = frameY;
         int frameWidth = img.frameWidth();
         int frameHeight = img.frameHeight();
         if (rotation == Rotation.CLOCKWISE_90) {
            newFrameX = frameHeight - frameY - 1;
            newFrameY = frameX;
         } else if (rotation == Rotation.CLOCKWISE_180) {
            newFrameX = frameWidth - frameX - 1;
            newFrameY = frameHeight - frameY - 1;
         } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            newFrameX = frameY;
            newFrameY = frameWidth - frameX - 1;
         }

         int newGlobalX = flippedImage.getFrameStartX(frameIndex) + newFrameX;
         int newGlobalY = flippedImage.getFrameStartY(frameIndex) + newFrameY;
         int pixel = context.getValue();
         flippedImage.setPixel(newGlobalX, newGlobalY, pixel);
      });
      return flippedImage;
   }
}
