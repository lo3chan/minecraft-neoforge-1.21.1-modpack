package net.mehvahdjukaar.moonlight.api.resources.textures;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.McMetaFile;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.util.FastColor.ABGR32;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Respriter {
   private final TextureImage imageToRecolor;
   private final Palette originalPalette;
   @Nullable
   private final Sampler2D recoloringMask;

   public static Respriter of(TextureImage imageToRecolor) {
      return new Respriter(imageToRecolor, Palette.fromImage(imageToRecolor, null, 0.0F), null);
   }

   public static Respriter masked(TextureImage imageToRecolor, TextureImage colorMask) {
      return new Respriter(imageToRecolor, Palette.fromImage(imageToRecolor, colorMask, 0.0F), colorMask);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Respriter ofPalette(TextureImage imageToRecolor, List<Palette> colorsToSwap) {
      return new Respriter(imageToRecolor, Palette.merge(colorsToSwap.toArray(Palette[]::new)), null);
   }

   public static Respriter ofPalette(TextureImage imageToRecolor, Palette colorsToSwap) {
      return new Respriter(imageToRecolor, colorsToSwap, null);
   }

   private Respriter(TextureImage imageToRecolor, Palette colorsToSwap, @Nullable Sampler2D recoloringMask) {
      if (colorsToSwap.isEmpty()) {
         throw new UnsupportedOperationException("Respriter must have a non empty target palette");
      } else {
         this.imageToRecolor = imageToRecolor;
         this.originalPalette = colorsToSwap;
         this.recoloringMask = recoloringMask;
      }
   }

   public TextureImage recolorWithAnimationOf(TextureImage textureImage) {
      return this.recolorWithAnimation(List.of(Palette.fromImage(textureImage)), textureImage.getMcMeta());
   }

   @Deprecated(
      forRemoval = true
   )
   public TextureImage recolorWithAnimation(List<Palette> targetPalettes, @Nullable AnimationMetadataSection targetAnimationData) {
      return this.recolorWithAnimation(targetPalettes, targetAnimationData == null ? null : McMetaFile.of(targetAnimationData));
   }

   public TextureImage recolorWithAnimation(List<Palette> targetPalettes, @Nullable McMetaFile targetAnimationData) {
      if (!this.imageToRecolor.isAllocated()) {
         Moonlight.crashIfInDev("Respriter was given a non allocated image!");
      }

      if (targetPalettes.isEmpty()) {
         Moonlight.crashIfInDev("Respriter was given no palettes!");
         return this.imageToRecolor.makeCopy();
      } else {
         McMetaFile mergedMcMeta = McMetaFile.merge(this.imageToRecolor.getMcMeta(), targetAnimationData);
         int originalFrameCount = this.imageToRecolor.frameCount();
         boolean turnsIntoAnimation = originalFrameCount == 1 && mergedMcMeta != null && mergedMcMeta.hasAnimation();
         TextureImage outputTexture;
         if (turnsIntoAnimation) {
            int stripLength = Math.max(targetPalettes.size(), mergedMcMeta.requiredFrameCount());
            outputTexture = TextureOps.createSingleFrameAnimation(this.imageToRecolor, stripLength, mergedMcMeta);
         } else {
            outputTexture = this.imageToRecolor.makeCopyWithMetadata(mergedMcMeta);
         }

         Respriter.FrameColorRemapper colorRemapper = Respriter.FrameColorRemapper.of(
            this.originalPalette, originalFrameCount, targetPalettes, outputTexture.frameCount()
         );
         outputTexture.forEachPixel(pixel -> {
            int ind = pixel.frameIndex();
            if (this.recoloringMask == null || ABGR32.alpha(this.recoloringMask.sample(pixel.globalX, pixel.globalY)) == 0) {
               Integer newColor = colorRemapper.remapColor(ind, pixel.getValue());
               if (newColor != null) {
                  pixel.setValue(newColor);
               }
            }
         });
         return outputTexture;
      }
   }

   public TextureImage recolor(List<Palette> targetPalettes) {
      return this.recolorWithAnimation(targetPalettes, (McMetaFile)null);
   }

   public TextureImage recolor(Palette targetPalette) {
      return this.recolor(List.of(targetPalette));
   }

   private record Color2ColorMap(Int2ObjectArrayMap<Integer> map) {
      static final Respriter.Color2ColorMap EMPTY = new Respriter.Color2ColorMap(new Int2ObjectArrayMap(0));

      @Nullable
      public Integer mapColor(int color) {
         return (Integer)this.map.get(color);
      }

      @NotNull
      public static Respriter.Color2ColorMap create(Palette originalPalette, Palette toPalette) {
         toPalette = toPalette.copy();
         toPalette.matchSize(originalPalette.size(), originalPalette.getAverageLuminanceStep());
         if (toPalette.size() != originalPalette.size()) {
            Moonlight.LOGGER.error("Failed to create Color2ColorMap. Too few colors in toPalette: {} vs required {}", toPalette.size(), originalPalette.size());
            return EMPTY;
         } else {
            return new Respriter.Color2ColorMap(zipToMap(originalPalette.getValues(), toPalette.getValues()));
         }
      }

      private static Int2ObjectArrayMap<Integer> zipToMap(List<PaletteColor> keys, List<PaletteColor> values) {
         Int2ObjectArrayMap<Integer> map = new Int2ObjectArrayMap(keys.size());

         for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i).value(), values.get(i).value());
         }

         return map;
      }
   }

   @FunctionalInterface
   private interface FrameColorRemapper {
      static Respriter.FrameColorRemapper of(Palette originalPalette, int originalFrameCount, List<Palette> targetPalettes, int targetFrameCount) {
         boolean invalidSize = targetFrameCount > targetPalettes.size();
         if (originalFrameCount == 1 && !invalidSize) {
            List<Respriter.Color2ColorMap> mappingPerFrame = new ArrayList<>();

            for (int i = 0; i < targetFrameCount; i++) {
               Palette toPalette = targetPalettes.get(i);
               mappingPerFrame.add(Respriter.Color2ColorMap.create(originalPalette, toPalette));
            }

            return (frameIndex, color) -> {
               Respriter.Color2ColorMap colorMap = mappingPerFrame.get(frameIndex);
               return colorMap != null ? colorMap.mapColor(color) : null;
            };
         } else {
            Palette firstPalette = (Palette)targetPalettes.getFirst();
            Respriter.Color2ColorMap singleColorMap = Respriter.Color2ColorMap.create(originalPalette, firstPalette);
            return (frameIndex, color) -> singleColorMap.mapColor(color);
         }
      }

      @Nullable
      Integer remapColor(int var1, int var2);
   }
}
