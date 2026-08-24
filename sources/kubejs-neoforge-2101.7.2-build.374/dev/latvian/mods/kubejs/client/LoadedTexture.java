package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.color.KubeColor;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;

public class LoadedTexture {
   public static final LoadedTexture EMPTY = new LoadedTexture(0, 0, new int[0], null);
   public final int width;
   public final int height;
   public final int[] pixels;
   public final byte[] mcmeta;

   public static LoadedTexture load(ResourceLocation id) {
      try {
         Path path = KubeJSPaths.ASSETS.resolve(id.getNamespace() + "/textures/" + id.getPath() + ".png");
         if (Files.exists(path)) {
            LoadedTexture var20;
            try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path))) {
               Path metaPath = KubeJSPaths.ASSETS.resolve(id.getNamespace() + "/textures/" + id.getPath() + ".png.mcmeta");
               var20 = new LoadedTexture(ImageIO.read(in), Files.exists(metaPath) ? Files.readAllBytes(metaPath) : null);
            }

            return var20;
         }

         if (id.getNamespace().equals("kubejs")) {
            Path path1 = KubeJS.thisMod
               .getModInfo()
               .getOwningFile()
               .getFile()
               .findResource(new String[]{"assets", "kubejs", "textures", id.getPath() + ".png"});
            if (Files.exists(path1)) {
               LoadedTexture in;
               try (BufferedInputStream inx = new BufferedInputStream(Files.newInputStream(path1))) {
                  Path metaPath = KubeJS.thisMod
                     .getModInfo()
                     .getOwningFile()
                     .getFile()
                     .findResource(new String[]{"assets", "kubejs", "textures", id.getPath() + ".png.mcmeta"});
                  in = new LoadedTexture(ImageIO.read(inx), Files.exists(metaPath) ? Files.readAllBytes(metaPath) : null);
               }

               return in;
            }
         } else {
            Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(id.getNamespace());
            if (modContainer.isPresent()) {
               IModFile modFile = modContainer.get().getModInfo().getOwningFile().getFile();
               Path path2 = modFile.findResource(new String[]{"assets", id.getNamespace(), "textures", id.getPath() + ".png"});
               if (Files.exists(path2)) {
                  LoadedTexture var7;
                  try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path2))) {
                     Path metaPath = modFile.findResource(new String[]{"assets", id.getNamespace(), "textures", id.getPath() + ".png.mcmeta"});
                     var7 = new LoadedTexture(ImageIO.read(in), Files.exists(metaPath) ? Files.readAllBytes(metaPath) : null);
                  }

                  return var7;
               }
            }
         }
      } catch (IOException var14) {
         throw new RuntimeException(var14);
      }

      return EMPTY;
   }

   public LoadedTexture(int width, int height, int[] pixels, @Nullable byte[] mcmeta) {
      this.width = width;
      this.height = height;
      this.pixels = pixels;
      this.mcmeta = mcmeta;
   }

   public LoadedTexture(BufferedImage img, @Nullable byte[] mcmeta) {
      this.width = img.getWidth();
      this.height = img.getHeight();
      this.pixels = new int[this.width * this.height];
      img.getRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
      this.mcmeta = mcmeta;
   }

   public byte[] toBytes() {
      if (this.width != 0 && this.height != 0) {
         BufferedImage img = new BufferedImage(this.width, this.height, 2);
         img.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            ImageIO.write(img, "png", out);
         } catch (Exception var4) {
            throw new RuntimeException(var4);
         }

         return out.toByteArray();
      } else {
         return new byte[0];
      }
   }

   public LoadedTexture copy() {
      return new LoadedTexture(this.width, this.height, (int[])this.pixels.clone(), this.mcmeta);
   }

   public LoadedTexture remap(Map<KubeColor, KubeColor> remap) {
      if (remap.isEmpty()) {
         return this;
      } else {
         Int2IntArrayMap colorMap = new Int2IntArrayMap(remap.size());

         for (Entry<KubeColor, KubeColor> entry : remap.entrySet()) {
            KubeColor k = entry.getKey();
            KubeColor v = entry.getValue();
            colorMap.put(k.kjs$getARGB(), v.kjs$getARGB());
         }

         int[] result = new int[this.pixels.length];

         for (int i = 0; i < this.pixels.length; i++) {
            result[i] = (this.pixels[i] & 0xFF000000) == 0 ? 0 : colorMap.getOrDefault(this.pixels[i], this.pixels[i]);
         }

         return new LoadedTexture(this.width, this.height, result, this.mcmeta);
      }
   }

   public LoadedTexture resize(int newWidth, int newHeight) {
      if (this.width == newWidth && this.height == newHeight) {
         return this;
      } else {
         BufferedImage source = new BufferedImage(this.width, this.height, 2);
         source.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
         BufferedImage dst = new BufferedImage(newWidth, newHeight, 2);
         Graphics2D bg = dst.createGraphics();
         bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
         float sx = (float)newWidth / this.width;
         float sy = (float)newHeight / this.height;
         bg.scale(sx, sy);
         bg.drawImage(source, 0, 0, null);
         bg.dispose();
         return new LoadedTexture(dst, this.mcmeta);
      }
   }

   public LoadedTexture tint(@Nullable KubeColor tint) {
      if (tint == null) {
         return this;
      } else {
         int argb = tint.kjs$getARGB();
         float l = (argb >> 24 & 0xFF) / 255.0F;
         if (l <= 0.0F) {
            return this;
         } else {
            if (l > 1.0F) {
               l = 1.0F;
            }

            float tr = (argb >> 16 & 0xFF) / 255.0F;
            float tg = (argb >> 8 & 0xFF) / 255.0F;
            float tb = (argb & 0xFF) / 255.0F;
            int[] result = new int[this.pixels.length];

            for (int i = 0; i < this.pixels.length; i++) {
               float pr = (this.pixels[i] >> 16 & 0xFF) / 255.0F;
               float pg = (this.pixels[i] >> 8 & 0xFF) / 255.0F;
               float pb = (this.pixels[i] & 0xFF) / 255.0F;
               result[i] = this.pixels[i] & 0xFF000000
                  | (int)(Mth.lerp(l, pr, pr * tr) * 255.0F) << 16
                  | (int)(Mth.lerp(l, pg, pg * tg) * 255.0F) << 8
                  | (int)(Mth.lerp(l, pb, pb * tb) * 255.0F);
            }

            return new LoadedTexture(this.width, this.height, result, this.mcmeta);
         }
      }
   }
}
