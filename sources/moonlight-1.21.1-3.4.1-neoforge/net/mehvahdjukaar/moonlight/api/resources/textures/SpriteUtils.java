package net.mehvahdjukaar.moonlight.api.resources.textures;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;
import net.mehvahdjukaar.moonlight.api.util.math.colors.HSVColor;
import net.mehvahdjukaar.moonlight.api.util.math.colors.RGBColor;
import net.mehvahdjukaar.moonlight.api.util.math.kmeans.DataSet;
import net.mehvahdjukaar.moonlight.api.util.math.kmeans.IDataEntry;
import net.mehvahdjukaar.moonlight.api.util.math.kmeans.KMeans;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class SpriteUtils {
   private static final byte[] PNG_SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};

   public static NativeImage readImage(ResourceManager manager, ResourceLocation resourceLocation) throws IOException, NoSuchElementException {
      try {
         NativeImage var3;
         try (InputStream res = ((Resource)manager.getResource(resourceLocation).get()).open()) {
            var3 = NativeImage.read(res);
         }

         return var3;
      } catch (Exception var7) {
         throw new IOException(var7);
      }
   }

   public static NativeImage readImage(byte[] imageBytes) throws IOException {
      if (isPng(imageBytes)) {
         NativeImage var2;
         try (InputStream in = new ByteArrayInputStream(imageBytes)) {
            var2 = NativeImage.read(in);
         }

         return var2;
      } else {
         return readImageWithStb(imageBytes);
      }
   }

   private static boolean isPng(byte[] bytes) {
      if (bytes.length < PNG_SIGNATURE.length) {
         return false;
      } else {
         for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            if (bytes[i] != PNG_SIGNATURE[i]) {
               return false;
            }
         }

         return true;
      }
   }

   private static NativeImage readImageWithStb(byte[] imageBytes) throws IOException {
      ByteBuffer encoded = MemoryUtil.memAlloc(imageBytes.length);

      NativeImage var8;
      try {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            encoded.put(imageBytes).flip();
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer pixels = STBImage.stbi_load_from_memory(encoded, width, height, channels, 4);
            if (pixels == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            try {
               NativeImage image = new NativeImage(Format.RGBA, width.get(0), height.get(0), false);
               MemoryUtil.memCopy(MemoryUtil.memAddress(pixels), image.pixels, (long)width.get(0) * height.get(0) * 4L);
               var8 = image;
            } finally {
               STBImage.stbi_image_free(pixels);
            }
         } catch (Throwable var21) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var19) {
                  var21.addSuppressed(var19);
               }
            }

            throw var21;
         }

         if (stack != null) {
            stack.close();
         }
      } finally {
         MemoryUtil.memFree(encoded);
      }

      return var8;
   }

   public static void forEachPixel(NativeImage image, BiConsumer<Integer, Integer> function) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            function.accept(x, y);
         }
      }
   }

   public static void grayscaleImage(NativeImage image) {
      forEachPixel(image, (x, y) -> image.setPixelRGBA(x, y, new RGBColor(image.getPixelRGBA(x, y)).asHCL().withChroma(0.0F).asRGB().toInt()));
   }

   public static RGBColor averageColor(NativeImage image) {
      Palette p = Palette.fromImage(TextureImage.of(image), null, 0.0F);
      if (p.isEmpty()) {
         return new RGBColor(-1);
      } else {
         DataSet<DataSet.ColorPoint> data = DataSet.fromPalette(p);
         KMeans.kMeans(data, 1);
         return ((DataSet.ColorPoint)((IDataEntry)data.getLastCentroids().getFirst()).cast()).getColor().rgb();
      }
   }

   public static List<Palette> extrapolateSignBlockPalette(TextureImage planksTexture) {
      List<Palette> newPalettes = new ArrayList<>();

      for (Palette palette : Palette.fromAnimatedImage(planksTexture, null, 0.0033333334F)) {
         extrapolateSignBlockPalette(palette);
         newPalettes.add(palette);
      }

      return newPalettes;
   }

   public static void extrapolateSignBlockPalette(Palette palette) {
      int size = palette.size();
      if (size == 7) {
         PaletteColor color = palette.get(size - 3);
         HSVColor hsv = color.rgb().asHSV();
         float satIncrease = 1.0638298F;
         float brightnessIncrease = 1.0638298F;
         HSVColor newCol = new HSVColor(
            hsv.hue(), Mth.clamp(hsv.saturation() * satIncrease, 0.0F, 1.0F), Mth.clamp(hsv.value() * brightnessIncrease, 0.0F, 1.0F), hsv.alpha()
         );
         PaletteColor newP = new PaletteColor(newCol);
         newP.setOccurrence(color.getOccurrence());
         palette.set(size - 1, newP);
         palette.remove(size - 2);
      }
   }

   public static Palette extrapolateWoodItemPalette(TextureImage planksTexture) {
      Palette palette = (Palette)Palette.fromAnimatedImage(planksTexture, null).getFirst();
      extrapolateWoodItemPalette(palette);
      return palette;
   }

   public static void extrapolateWoodItemPalette(Palette palette) {
      PaletteColor color = palette.get(0);
      HSVColor hsv = color.rgb().asHSV();
      float satMult = 1.11F;
      float brightnessMult = 0.94F;
      HSVColor newCol = new HSVColor(
         hsv.hue(), Mth.clamp(hsv.saturation() * satMult, 0.0F, 1.0F), Mth.clamp(hsv.value() * brightnessMult, 0.0F, 1.0F), hsv.alpha()
      );
      PaletteColor newP = new PaletteColor(newCol);
      newP.setOccurrence(color.getOccurrence());
      palette.set(0, newP);
   }

   @Deprecated
   public static float getLuminance(int r, int g, int b) {
      return 0.299F * r + 0.587F * g + 0.114F * b;
   }

   public static void reduceColors(NativeImage image, IntUnaryOperator sizeFn) {
      Palette p = Palette.fromImage(TextureImage.of(image), null, 0.0F);
      if (!p.isEmpty()) {
         DataSet<DataSet.ColorPoint> data = DataSet.fromPalette(p);
         int size = sizeFn.applyAsInt(p.size());
         if (size < p.size()) {
            KMeans.kMeans(data, size);
            Map<Integer, Integer> colorToColorMap = new HashMap<>();

            for (IDataEntry<DataSet.ColorPoint> c : data.getColorPoints()) {
               IDataEntry<DataSet.ColorPoint> centroid = data.getLastCentroids().get(c.getClusterNo());
               colorToColorMap.put(c.cast().getColor().value(), centroid.cast().getColor().value());
            }

            forEachPixel(image, (x, y) -> {
               int i = image.getPixelRGBA(x, y);
               if (colorToColorMap.containsKey(i)) {
                  image.setPixelRGBA(x, y, colorToColorMap.get(i));
               }
            });
         }
      }
   }

   public static void mergeSimilarColors(NativeImage image, float tolerance) {
      TextureImage texture = TextureImage.of(image);
      Palette originalPalette = Palette.fromImage(texture, null, 0.0F);
      Palette targetPalette = originalPalette.copy();
      targetPalette.updateTolerance(tolerance);
      originalPalette.removeAll(targetPalette);
      Map<Integer, Integer> removedColors = new HashMap<>();

      for (PaletteColor i : originalPalette) {
         PaletteColor replacement = targetPalette.getColorClosestTo(i);
         removedColors.put(i.value(), replacement.value());
      }

      forEachPixel(image, (x, y) -> {
         int ix = image.getPixelRGBA(x, y);
         Integer replacementx = removedColors.get(ix);
         if (replacementx != null) {
            image.setPixelRGBA(x, y, replacementx);
         }
      });
   }

   public static List<Integer> parsePaletteStrip(ResourceManager manager, ResourceLocation fullTexturePath, int expectColors) {
      try {
         NativeImage image = readImage(manager, fullTexturePath);

         Object var5;
         try {
            List<Integer> list = new ArrayList<>();
            forEachPixel(image, (x, y) -> {
               int i = image.getPixelRGBA(x, y);
               if (i != 0 && list.size() < expectColors) {
                  list.add(i);
               }
            });
            if (list.size() < expectColors) {
               throw new RuntimeException("Image at " + fullTexturePath + " has too few colors! Expected at least " + expectColors + " and got " + list.size());
            }

            var5 = list;
         } catch (Throwable var7) {
            if (image != null) {
               try {
                  image.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (image != null) {
            image.close();
         }

         return (List<Integer>)var5;
      } catch (NoSuchElementException | IOException var8) {
         throw new RuntimeException("Failed to find image at location " + fullTexturePath, var8);
      }
   }

   public static TextureImage savePaletteStrip(ResourceManager manager, List<Integer> colors) {
      try {
         TextureImage var4;
         try (TextureImage image = TextureImage.createNew(16, 16)) {
            Iterator<Integer> it = colors.iterator();
            image.forEachPixel(pixel -> {
               if (it.hasNext()) {
                  pixel.setValue(it.next());
               }
            });
            var4 = image;
         }

         return var4;
      } catch (Exception var7) {
         throw new RuntimeException("Failed to create palette strip");
      }
   }
}
