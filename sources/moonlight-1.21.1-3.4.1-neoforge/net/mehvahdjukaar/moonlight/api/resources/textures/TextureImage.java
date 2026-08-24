package net.mehvahdjukaar.moonlight.api.resources.textures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.util.math.colors.RGBColor;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.McMetaFile;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class TextureImage implements AutoCloseable, Sampler2D {
   @Nullable
   private final McMetaFile metadata;
   private final NativeImage image;
   private final FrameSize frameSize;
   private final int frameCount;
   private final int frameScale;
   final String debugPath;

   public static TextureImage open(ResourceManager manager, ResourceLocation relativePath) throws IOException {
      try {
         if (relativePath.getPath().endsWith(".png")) {
            relativePath = relativePath.withPath(relativePath.getPath().substring(0, relativePath.getPath().length() - 4));
         }

         ResourceLocation textureLoc = ResType.TEXTURES.getPath(relativePath);
         NativeImage i = SpriteUtils.readImage(manager, textureLoc);
         ResourceLocation metadataLoc = ResType.MCMETA.getPath(relativePath);
         McMetaFile metadata = null;
         Optional<Resource> res = manager.getResource(metadataLoc);
         if (res.isPresent()) {
            try {
               metadata = McMetaFile.read(res.get());
            } catch (Exception var8) {
               throw new IOException("Failed to open texture at location " + relativePath + ": failed to read mcmeta file", var8);
            }
         }

         return new TextureImage(i, metadata, relativePath.toString());
      } catch (Exception var9) {
         TextureImage virtual = PalettedPermutationsHelper.tryResolve(manager, relativePath);
         if (virtual != null) {
            return virtual;
         } else {
            throw new IOException("Failed to open texture at location " + relativePath + ": no such file");
         }
      }
   }

   public static TextureImage createNew(int width, int height) {
      return createNew(width, height, (McMetaFile)null);
   }

   public static TextureImage createNew(int width, int height, @Nullable McMetaFile metadata) {
      if (width > 0 && height > 0) {
         TextureImage v = new TextureImage(new NativeImage(width, height, false), metadata);
         v.clear();
         return v;
      } else {
         throw new IllegalArgumentException("Width and height must be positive integers");
      }
   }

   public static TextureImage of(NativeImage image) {
      return of(image, (McMetaFile)null);
   }

   public static TextureImage of(NativeImage image, @Nullable McMetaFile metadata) {
      return new TextureImage(image, metadata);
   }

   private TextureImage(NativeImage image, @Nullable McMetaFile metadata) {
      this(image, metadata, "unknown");
   }

   private TextureImage(NativeImage image, @Nullable McMetaFile metadata, String debugPath) {
      this.image = image;
      this.metadata = metadata;
      this.debugPath = debugPath;
      int imgWidth = this.imageWidth();
      int imgHeight = this.imageHeight();
      FrameSize metaSize = metadata != null && metadata.hasAnimation()
         ? metadata.animation().calculateFrameSize(imgWidth, imgHeight)
         : new FrameSize(imgWidth, imgHeight);
      int fw = metaSize.width();
      int fh = metaSize.height();
      if (fw <= 0 || fh <= 0) {
         Moonlight.LOGGER
            .error("Texture '{}' has invalid metadata frame size {}x{}, using full image size {}x{} instead", debugPath, fw, fh, imgWidth, imgHeight);
         fw = imgWidth;
         fh = imgHeight;
      }

      if (fw > imgWidth || fh > imgHeight) {
         Moonlight.LOGGER
            .error("Texture '{}' frame size {}x{} is larger than image {}x{}, using full image size instead", debugPath, fw, fh, imgWidth, imgHeight);
         fw = imgWidth;
         fh = imgHeight;
      }

      this.frameSize = new FrameSize(fw, fh);
      int gridW = imgWidth / fw;
      int gridH = imgHeight / fh;
      if (gridW == 0 || gridH == 0) {
         Moonlight.LOGGER.error("Texture '{}' frame size {}x{} cannot fit in image {}x{}, defaulting to 1x1 grid", debugPath, fw, fh, imgWidth, imgHeight);
         gridW = 1;
         gridH = 1;
      }

      this.frameScale = gridW;
      this.frameCount = gridW * gridH;
   }

   public int imageWidth() {
      return this.image.getWidth();
   }

   public int imageHeight() {
      return this.image.getHeight();
   }

   public int frameCount() {
      return this.frameCount;
   }

   public int frameWidth() {
      return this.frameSize.width();
   }

   public int frameHeight() {
      return this.frameSize.height();
   }

   public McMetaFile getMcMeta() {
      return this.metadata;
   }

   @Internal
   public NativeImage getImage() {
      return this.image;
   }

   @Override
   public String toString() {
      return "TextureImage{" + this.debugPath + ", allocated = " + this.isAllocated() + "}";
   }

   public boolean isAllocated() {
      return this.image.pixels != 0L;
   }

   public int getFrameStartX(int frameIndex) {
      return frameIndex % this.frameScale * this.frameWidth();
   }

   public int getFrameStartY(int frameIndex) {
      return frameIndex / this.frameScale * this.frameHeight();
   }

   public int getFramePixel(int frameIndex, int x, int y) {
      return this.image.getPixelRGBA(this.getFrameStartX(frameIndex) + x, this.getFrameStartY(frameIndex) + y);
   }

   public int getPixel(int x, int y) {
      return this.image.getPixelRGBA(x, y);
   }

   @Override
   public int sample(float x, float y) {
      int ix = Mth.clamp(Math.round(x), 0, this.imageWidth() - 1);
      int iy = Mth.clamp(Math.round(y), 0, this.imageHeight() - 1);
      return this.getPixel(ix, iy);
   }

   public Sampler2D frameSampler(int frameIndex) {
      return (x, y) -> {
         int ix = Mth.clamp(Math.round(x), 0, this.frameWidth() - 1);
         int iy = Mth.clamp(Math.round(y), 0, this.frameHeight() - 1);
         return this.getFramePixel(frameIndex, ix, iy);
      };
   }

   public void setFramePixel(int frameIndex, int x, int y, int color) {
      this.image.setPixelRGBA(this.getFrameStartX(frameIndex) + x, this.getFrameStartY(frameIndex) + y, color);
   }

   public void setPixel(int x, int y, int color) {
      this.image.setPixelRGBA(x, y, color);
   }

   public void blendPixel(int x, int y, int color) {
      this.image.blendPixel(x, y, color);
   }

   public void blendFramePixel(int frameIndex, int x, int y, int color) {
      this.image.blendPixel(this.getFrameStartX(frameIndex) + x, this.getFrameStartY(frameIndex) + y, color);
   }

   public void forEachPixel(Consumer<PixelContext> consumer) {
      PixelContext pixel = new PixelContext(this);

      for (int frameIdx = 0; frameIdx < this.frameCount; frameIdx++) {
         int xOff = this.getFrameStartX(frameIdx);
         int yOff = this.getFrameStartY(frameIdx);

         for (int x = 0; x < this.frameWidth(); x++) {
            for (int y = 0; y < this.frameHeight(); y++) {
               pixel.frameIndex = frameIdx;
               pixel.localX = x;
               pixel.localY = y;
               pixel.globalX = x + xOff;
               pixel.globalY = y + yOff;
               consumer.accept(pixel);
            }
         }
      }
   }

   public TextureImage makeCopy() {
      return this.makeCopyWithMetadata(this.metadata);
   }

   public TextureImage makeCopyWithMetadata(McMetaFile mcMetaFile) {
      NativeImage im = new NativeImage(this.imageWidth(), this.imageHeight(), false);
      im.copyFrom(this.image);
      return new TextureImage(im, mcMetaFile);
   }

   @Override
   public void close() {
      this.image.close();
   }

   public void doAndClose(TextureImage.ThrowingRunnable action) {
      try {
         TextureImage e = this;

         try {
            action.run();
         } catch (Throwable var6) {
            if (this != null) {
               try {
                  e.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (this != null) {
            this.close();
         }
      } catch (Exception var7) {
         throw new RuntimeException(var7);
      }
   }

   public void clear() {
      this.image.fillRect(0, 0, this.image.getWidth(), this.image.getHeight(), 0);
   }

   @Deprecated(
      forRemoval = true
   )
   public RGBColor getAverageColor() {
      return SpriteUtils.averageColor(this.image);
   }

   public ImmutableList<NativeImage> splitFrames() {
      Builder<NativeImage> builder = ImmutableList.builder();
      int imgWidth = this.imageWidth();
      int imgHeight = this.imageHeight();
      int fw = this.frameWidth();
      int fh = this.frameHeight();
      int gridW = imgWidth / fw;
      int gridH = imgHeight / fh;
      int maxFrames = gridW * gridH;
      if (this.metadata != null && this.metadata.hasAnimation() && !this.metadata.animation().frames.isEmpty()) {
         List<Integer> indexList = Lists.newArrayList();
         this.metadata.animation().forEachFrame((indexx, time) -> indexList.add(indexx));
         if (indexList.isEmpty()) {
            for (int i = 0; i < maxFrames; i++) {
               indexList.add(i);
            }
         }

         if (indexList.size() <= 1) {
            builder.add(this.image);
            return builder.build();
         } else {
            for (int index : indexList) {
               if (index >= 0 && index < maxFrames) {
                  int xOffset = index % gridW * fw;
                  int yOffset = index / gridW * fh;
                  if (xOffset + fw <= imgWidth && yOffset + fh <= imgHeight) {
                     NativeImage frame = new NativeImage(fw, fh, false);

                     for (int x = 0; x < fw; x++) {
                        for (int y = 0; y < fh; y++) {
                           frame.setPixelRGBA(x, y, this.image.getPixelRGBA(x + xOffset, y + yOffset));
                        }
                     }

                     builder.add(frame);
                  }
               }
            }

            return builder.build();
         }
      } else {
         builder.add(this.image);
         return builder.build();
      }
   }

   @Deprecated
   public void toGrayscale() {
      TextureOps.grayscale(this);
   }

   @Deprecated(
      forRemoval = true
   )
   public TextureImage createAnimationTemplate(int length, McMetaFile useDataFrom) {
      return TextureOps.createSingleFrameAnimation(this, length, useDataFrom);
   }

   @Deprecated(
      forRemoval = true
   )
   public void applyOverlay(TextureImage... overlays) throws IllegalStateException {
      TextureOps.applyOverlay(this, overlays);
      Arrays.stream(overlays).forEach(TextureImage::close);
   }

   @Deprecated(
      forRemoval = true
   )
   public void applyOverlayOnExisting(TextureImage... overlays) throws IllegalStateException {
      TextureOps.applyOverlayOnExisting(this, overlays);
      Arrays.stream(overlays).forEach(TextureImage::close);
   }

   @Deprecated(
      forRemoval = true
   )
   public void removeAlpha(int backgroundColor) {
      TextureOps.makeOpaque(this, backgroundColor);
   }

   @Deprecated(
      forRemoval = true
   )
   public TextureImage createRotated(Rotation rotation) {
      return TextureOps.createRotated(this, rotation);
   }

   @Deprecated(
      forRemoval = true
   )
   public TextureImage createResized(float widthScale, float heightScale) {
      return TextureOps.createScaled(this, widthScale, heightScale);
   }

   @Deprecated(
      forRemoval = true
   )
   public void crop(TextureImage mask) {
      this.crop(mask, true);
   }

   @Deprecated(
      forRemoval = true
   )
   public void crop(TextureImage mask, boolean discardInner) {
      if (discardInner) {
         TextureOps.applyMask(this, mask);
      } else {
         TextureOps.applyMaskInverted(this, mask);
      }

      mask.close();
   }

   @Deprecated(
      forRemoval = true
   )
   public TextureImage createAnimationTemplate(int length, @NotNull AnimationMetadataSection useDataFrom) {
      return this.createAnimationTemplate(length, McMetaFile.of(useDataFrom));
   }

   @Deprecated(
      forRemoval = true
   )
   public TextureImage createAnimationTemplate(int length, List<AnimationFrame> frameData, int frameTime, boolean interpolate) {
      return this.createAnimationTemplate(length, new AnimationMetadataSection(frameData, this.frameWidth(), this.frameHeight(), frameTime, interpolate));
   }

   @Deprecated(
      forRemoval = true
   )
   public void forEachFrame(TextureImage.FramePixelConsumer e) {
      this.forEachFramePixel(e);
   }

   @Deprecated(
      forRemoval = true
   )
   public void forEachFramePixel(TextureImage.FramePixelConsumer framePixelConsumer) {
      for (int ind = 0; ind < this.frameCount; ind++) {
         int xOff = this.getFrameStartX(ind);
         int yOff = this.getFrameStartY(ind);

         for (int x = 0; x < this.frameWidth(); x++) {
            for (int y = 0; y < this.frameHeight(); y++) {
               framePixelConsumer.accept(ind, x + xOff, y + yOff);
            }
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public AnimationMetadataSection getMetadata() {
      return this.metadata == null ? null : this.metadata.animation();
   }

   @Deprecated(
      forRemoval = true
   )
   public static TextureImage createNew(int width, int height, @Nullable AnimationMetadataSection animation) {
      return createNew(width, height, animation == null ? null : McMetaFile.of(animation));
   }

   @Deprecated(
      forRemoval = true
   )
   public static TextureImage of(NativeImage image, @Nullable AnimationMetadataSection animation) {
      return of(image, animation == null ? null : McMetaFile.of(animation));
   }

   @Deprecated(
      forRemoval = true
   )
   @FunctionalInterface
   public interface FramePixelConsumer extends TriConsumer<Integer, Integer, Integer> {
      void accept(Integer var1, Integer var2, Integer var3);
   }

   @FunctionalInterface
   public interface ThrowingRunnable {
      void run() throws Exception;
   }
}
