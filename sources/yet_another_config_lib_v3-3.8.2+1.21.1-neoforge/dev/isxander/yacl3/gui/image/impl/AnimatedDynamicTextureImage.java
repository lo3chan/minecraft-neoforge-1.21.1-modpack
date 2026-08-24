package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import dev.isxander.yacl3.debug.DebugProperties;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class AnimatedDynamicTextureImage extends DynamicTextureImage {
   private int currentFrame;
   private double lastFrameTime;
   private final double[] frameDelays;
   private final int frameCount;
   private final int packCols;
   private final int packRows;
   private final int frameWidth;
   private final int frameHeight;

   public AnimatedDynamicTextureImage(
      NativeImage image, int frameWidth, int frameHeight, int frameCount, double[] frameDelayMS, int packCols, int packRows, ResourceLocation uniqueLocation
   ) {
      super(image, uniqueLocation, false);
      this.frameWidth = frameWidth;
      this.frameHeight = frameHeight;
      this.frameCount = frameCount;
      this.frameDelays = frameDelayMS;
      this.packCols = packCols;
      this.packRows = packRows;
   }

   @Override
   public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
      if (this.image == null) {
         return 0;
      } else {
         float ratio = (float)renderWidth / this.frameWidth;
         int targetHeight = (int)(this.frameHeight * ratio);
         int currentCol = this.currentFrame % this.packCols;
         int currentRow = (int)Math.floor((double)this.currentFrame / this.packCols);
         GuiUtils.pushPose(graphics);
         GuiUtils.translate2D(graphics, x, y);
         GuiUtils.scale2D(graphics, ratio, ratio);
         GuiUtils.blitGuiTex(
            graphics,
            this.uniqueLocation,
            0,
            0,
            this.frameWidth * currentCol,
            this.frameHeight * currentRow,
            this.frameWidth,
            this.frameHeight,
            this.width,
            this.height,
            DebugProperties.IMAGE_FILTERING
         );
         GuiUtils.popPose(graphics);
         if (this.frameCount > 1) {
            double timeMS = Blaze3D.getTime() * 1000.0;
            if (this.lastFrameTime == 0.0) {
               this.lastFrameTime = timeMS;
            }

            if (timeMS - this.lastFrameTime >= this.frameDelays[this.currentFrame]) {
               this.currentFrame++;
               this.lastFrameTime = timeMS;
            }

            if (this.currentFrame >= this.frameCount - 1) {
               this.currentFrame = 0;
            }
         }

         return targetHeight;
      }
   }

   public static ImageRendererFactory createGIFFromTexture(ResourceLocation textureLocation) {
      return () -> {
         ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
         Resource resource = (Resource)resourceManager.getResource(textureLocation).orElseThrow();
         return createGIFSupplier(resource.open(), textureLocation);
      };
   }

   public static ImageRendererFactory createGIFFromPath(Path path, ResourceLocation uniqueLocation) {
      return () -> createGIFSupplier(new FileInputStream(path.toFile()), uniqueLocation);
   }

   public static ImageRendererFactory createWEBPFromTexture(ResourceLocation textureLocation) {
      return () -> {
         ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
         Resource resource = (Resource)resourceManager.getResource(textureLocation).orElseThrow();
         return createWEBPSupplier(resource.open(), textureLocation);
      };
   }

   public static ImageRendererFactory createWEBPFromPath(Path path, ResourceLocation uniqueLocation) {
      return () -> createWEBPSupplier(new FileInputStream(path.toFile()), uniqueLocation);
   }

   private static ImageRendererFactory.ImageSupplier createGIFSupplier(InputStream is, ResourceLocation uniqueLocation) {
      try {
         InputStream e = is;

         ImageRendererFactory.ImageSupplier var5;
         try {
            ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
            reader.setInput(ImageIO.createImageInputStream(is));
            AnimatedDynamicTextureImage.AnimFrameProvider animFrameFunction = i -> {
               IIOMetadata metadata = reader.getImageMetadata(i);
               String metaFormatName = metadata.getNativeMetadataFormatName();
               IIOMetadataNode root = (IIOMetadataNode)metadata.getAsTree(metaFormatName);
               IIOMetadataNode graphicsControlExtensionNode = (IIOMetadataNode)root.getElementsByTagName("GraphicControlExtension").item(0);
               int delay = Integer.parseInt(graphicsControlExtensionNode.getAttribute("delayTime")) * 10;
               return new AnimatedDynamicTextureImage.AnimFrame(delay, 0, 0);
            };
            var5 = createFromImageReader(reader, animFrameFunction, uniqueLocation);
         } catch (Throwable var7) {
            if (is != null) {
               try {
                  e.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (is != null) {
            is.close();
         }

         return var5;
      } catch (Exception var8) {
         CrashReport crashReport = CrashReport.forThrowable(var8, "Failed to load GIF image");
         CrashReportCategory category = crashReport.addCategory("YACL Gui");
         category.setDetail("Image identifier", uniqueLocation.toString());
         throw new ReportedException(crashReport);
      }
   }

   private static ImageRendererFactory.ImageSupplier createWEBPSupplier(InputStream is, ResourceLocation uniqueLocation) {
      try {
         InputStream e = is;

         ImageRendererFactory.ImageSupplier var17;
         try {
            ImageReader reader = new WebPImageReaderSpi().createReaderInstance();
            reader.setInput(ImageIO.createImageInputStream(is));
            int numImages = reader.getNumImages(true);
            AnimatedDynamicTextureImage.AnimFrameProvider animFrameFunction = i -> null;
            if (numImages > 1) {
               Class<?> webpReaderClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReader");
               Field framesField = webpReaderClass.getDeclaredField("frames");
               framesField.setAccessible(true);
               List<?> frames = (List<?>)framesField.get(reader);
               Class<?> animationFrameClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.AnimationFrame");
               Field durationField = animationFrameClass.getDeclaredField("duration");
               durationField.setAccessible(true);
               Field boundsField = animationFrameClass.getDeclaredField("bounds");
               boundsField.setAccessible(true);
               animFrameFunction = i -> {
                  Rectangle bounds = (Rectangle)boundsField.get(frames.get(i));
                  return new AnimatedDynamicTextureImage.AnimFrame((Integer)durationField.get(frames.get(i)), bounds.x, bounds.y);
               };
            }

            var17 = createFromImageReader(reader, animFrameFunction, uniqueLocation);
         } catch (Throwable var13) {
            if (is != null) {
               try {
                  e.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (is != null) {
            is.close();
         }

         return var17;
      } catch (Throwable var14) {
         CrashReport crashReport = CrashReport.forThrowable(var14, "Failed to load WEBP image");
         CrashReportCategory category = crashReport.addCategory("YACL Gui");
         category.setDetail("Image identifier", uniqueLocation.toString());
         throw new ReportedException(crashReport);
      }
   }

   private static ImageRendererFactory.ImageSupplier createFromImageReader(
      ImageReader reader, AnimatedDynamicTextureImage.AnimFrameProvider animationProvider, ResourceLocation uniqueLocation
   ) throws Exception {
      if (reader.isSeekForwardOnly()) {
         throw new RuntimeException("Image reader is not seekable");
      } else {
         int frameCount = reader.getNumImages(true);
         int frameWidth = IntStream.range(0, frameCount).map(ix -> {
            try {
               return reader.getWidth(ix);
            } catch (IOException var3x) {
               throw new RuntimeException(var3x);
            }
         }).max().orElseThrow();
         int frameHeight = IntStream.range(0, frameCount).map(ix -> {
            try {
               return reader.getHeight(ix);
            } catch (IOException var3x) {
               throw new RuntimeException(var3x);
            }
         }).max().orElseThrow();
         double ratio = (double)frameWidth / frameHeight;
         int cols = (int)Math.ceil(Math.sqrt(frameCount) / Math.sqrt(ratio));
         int rows = (int)Math.ceil((double)frameCount / cols);
         NativeImage image = new NativeImage(Format.RGBA, frameWidth * cols, frameHeight * rows, false);
         BufferedImage bi = null;
         Graphics2D graphics = null;
         double[] frameDelays = new double[frameCount];

         for (int i = 0; i < frameCount; i++) {
            AnimatedDynamicTextureImage.AnimFrame frame = animationProvider.get(i);
            if (frameCount > 1) {
               frameDelays[i] = frame.durationMS;
            }

            if (bi == null) {
               bi = reader.read(i);
               graphics = bi.createGraphics();
            } else {
               BufferedImage deltaFrame = reader.read(i);
               graphics.drawImage(deltaFrame, frame.xOffset, frame.yOffset, null);
            }

            int xOffset = (frameWidth - bi.getWidth()) / 2;
            int yOffset = (frameHeight - bi.getHeight()) / 2;

            for (int w = 0; w < bi.getWidth(); w++) {
               for (int h = 0; h < bi.getHeight(); h++) {
                  int argb = bi.getRGB(w, h);
                  int col = i % cols;
                  int row = (int)Math.floor((double)i / cols);
                  GuiUtils.setPixelARGB(image, frameWidth * col + w + xOffset, frameHeight * row + h + yOffset, argb);
               }
            }
         }

         if (graphics != null) {
            graphics.dispose();
         }

         reader.dispose();
         return () -> new AnimatedDynamicTextureImage(image, frameWidth, frameHeight, frameCount, frameDelays, cols, rows, uniqueLocation);
      }
   }

   private record AnimFrame(int durationMS, int xOffset, int yOffset) {
   }

   @FunctionalInterface
   private interface AnimFrameProvider {
      AnimatedDynamicTextureImage.AnimFrame get(int var1) throws Exception;
   }
}
