package snownee.jade.api.ui;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Stretch;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Tile;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.NineSlice.Border;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.impl.ui.StyledElement;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.util.JadeCodecs;

public abstract class BoxStyle implements Cloneable {
   private static final int[] DEFAULT_PADDING = new int[]{4, 4, 4, 4};
   public static final Codec<BoxStyle.GradientBorder> GRADIENT_BORDER_CODEC = RecordCodecBuilder.create(
      i -> i.group(
            JadeCodecs.floatArrayCodec(4, Codec.FLOAT).optionalFieldOf("boxProgressOffset").forGetter($ -> Optional.ofNullable($.boxProgressOffset)),
            ColorPalette.CODEC.optionalFieldOf("boxProgressColors", ColorPalette.DEFAULT).forGetter($ -> $.boxProgressColors),
            JadeCodecs.intArrayCodec(4, Codec.INT).optionalFieldOf("padding").forGetter($ -> Optional.ofNullable($.padding)),
            Color.CODEC.optionalFieldOf("backgroundColor", -1).forGetter($ -> $.bgColor),
            JadeCodecs.intArrayCodec(4, Color.CODEC).fieldOf("borderColor").forGetter($ -> $.borderColor),
            Codec.FLOAT.optionalFieldOf("borderWidth", 1.0F).forGetter($ -> $.borderWidth),
            Codec.BOOL.optionalFieldOf("roundCorner").forGetter($ -> Optional.ofNullable($.roundCorner))
         )
         .apply(i, BoxStyle.GradientBorder::new)
   );
   public static final Codec<BoxStyle.SpriteBase> SPRITE_BASE_CODEC = RecordCodecBuilder.create(
      i -> i.group(
            JadeCodecs.floatArrayCodec(4, Codec.FLOAT).optionalFieldOf("boxProgressOffset").forGetter($ -> Optional.ofNullable($.boxProgressOffset)),
            ColorPalette.CODEC.optionalFieldOf("boxProgressColors", ColorPalette.DEFAULT).forGetter($ -> $.boxProgressColors),
            JadeCodecs.intArrayCodec(4, Codec.INT).optionalFieldOf("padding").forGetter($ -> Optional.ofNullable($.padding)),
            ResourceLocation.CODEC.fieldOf("sprite").forGetter($ -> $.sprite),
            ResourceLocation.CODEC.optionalFieldOf("withIconSprite").forGetter($ -> Optional.ofNullable($.withIconSprite))
         )
         .apply(i, BoxStyle.SpriteBase::new)
   );
   public static final Codec<BoxStyle> CODEC = Codec.either(GRADIENT_BORDER_CODEC, SPRITE_BASE_CODEC)
      .xmap(
         $ -> (BoxStyle)$.map(Function.identity(), Function.identity()),
         $ -> $ instanceof BoxStyle.GradientBorder ? Either.left((BoxStyle.GradientBorder)$) : Either.right((BoxStyle.SpriteBase)$)
      );
   public final float[] boxProgressOffset;
   public final int[] padding;
   public ColorPalette boxProgressColors;

   public BoxStyle(Optional<float[]> boxProgressOffset, ColorPalette boxProgressColors, Optional<int[]> padding) {
      this.boxProgressOffset = boxProgressOffset.orElse(null);
      this.boxProgressColors = boxProgressColors;
      this.padding = padding.orElseGet(() -> (int[])rec$.clone());
   }

   public static BoxStyle getNestedBox() {
      return IThemeHelper.get().theme().nestedBoxStyle;
   }

   public static BoxStyle getViewGroup() {
      return IThemeHelper.get().theme().viewGroupStyle;
   }

   public static BoxStyle.GradientBorder getTransparent() {
      return BoxStyle.GradientBorder.TRANSPARENT;
   }

   public static BoxStyle getSprite(ResourceLocation sprite, @Nullable int[] padding) {
      return new BoxStyle.SpriteBase(Optional.empty(), ColorPalette.DEFAULT, Optional.ofNullable(padding), sprite, Optional.empty());
   }

   public abstract void render(GuiGraphics var1, StyledElement var2, float var3, float var4, float var5, float var6, float var7);

   public abstract float borderWidth();

   public float boxProgressOffset(ScreenDirection dir) {
      return this.boxProgressOffset == null ? 0.0F : this.boxProgressOffset[dir.ordinal()];
   }

   public int padding(ScreenDirection dir) {
      return ((int[])MoreObjects.firstNonNull(this.padding, DEFAULT_PADDING))[dir.ordinal()];
   }

   public abstract BoxStyle clone();

   public boolean hasRoundCorner() {
      return false;
   }

   public static class GradientBorder extends BoxStyle {
      public static final BoxStyle.GradientBorder TRANSPARENT = new BoxStyle.GradientBorder(
         Optional.empty(), ColorPalette.DEFAULT, Optional.empty(), -1, new int[]{-1, -1, -1, -1}, 0.0F, Optional.of(false)
      );
      public static final BoxStyle.GradientBorder DEFAULT_NESTED_BOX = new BoxStyle.GradientBorder(
         Optional.empty(), ColorPalette.DEFAULT, Optional.empty(), -1, new int[]{-8355712, -8355712, -8355712, -8355712}, 1.0F, Optional.empty()
      );
      public static final BoxStyle.GradientBorder DEFAULT_VIEW_GROUP = new BoxStyle.GradientBorder(
         Optional.empty(),
         ColorPalette.DEFAULT,
         Optional.of(new int[]{2, 2, 2, 2}),
         1145324612,
         new int[]{1145324612, 1145324612, 1145324612, 1145324612},
         0.75F,
         Optional.empty()
      );
      public int bgColor;
      public int[] borderColor;
      public float borderWidth;
      @Nullable
      public Boolean roundCorner;

      private GradientBorder(
         Optional<float[]> boxProgressOffset,
         ColorPalette boxProgressColors,
         Optional<int[]> padding,
         int bgColor,
         int[] borderColor,
         float borderWidth,
         Optional<Boolean> roundCorner
      ) {
         super(boxProgressOffset, boxProgressColors, padding);
         this.bgColor = bgColor;
         this.borderColor = borderColor;
         this.borderWidth = borderWidth;
         this.roundCorner = roundCorner.orElse(null);
      }

      @Override
      public float borderWidth() {
         return this.borderWidth;
      }

      @Override
      public void render(GuiGraphics guiGraphics, StyledElement element, float x, float y, float w, float h, float alpha) {
         boolean roundCorner = this.hasRoundCorner();
         if (this.bgColor != -1) {
            int bg = IWailaConfig.IConfigOverlay.applyAlpha(this.bgColor, alpha);
            DisplayHelper.INSTANCE
               .drawGradientRect(
                  guiGraphics,
                  x + this.borderWidth,
                  y + this.borderWidth,
                  w - this.borderWidth - this.borderWidth,
                  h - this.borderWidth - this.borderWidth,
                  bg,
                  bg
               );
            if (roundCorner) {
               DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x, y - 1.0F, w, 1.0F, bg, bg);
               DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x, y + h, w, 1.0F, bg, bg);
               DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x - 1.0F, y, 1.0F, h, bg, bg);
               DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x + w, y, 1.0F, h, bg, bg);
            }
         }

         if (this.borderWidth > 0.0F) {
            int[] borderColors = new int[4];

            for (int i = 0; i < 4; i++) {
               if (this.borderColor[i] != -1) {
                  borderColors[i] = IWailaConfig.IConfigOverlay.applyAlpha(this.borderColor[i], alpha);
               }
            }

            DisplayHelper.INSTANCE
               .drawGradientRect(
                  guiGraphics, x, y + this.borderWidth, this.borderWidth, h - this.borderWidth - this.borderWidth, borderColors[0], borderColors[3]
               );
            DisplayHelper.INSTANCE
               .drawGradientRect(
                  guiGraphics,
                  x + w - this.borderWidth,
                  y + this.borderWidth,
                  this.borderWidth,
                  h - this.borderWidth - this.borderWidth,
                  borderColors[1],
                  borderColors[2]
               );
            DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x, y, w, this.borderWidth, borderColors[0], borderColors[1]);
            DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x, y + h - this.borderWidth, w, this.borderWidth, borderColors[3], borderColors[2]);
         }
      }

      public BoxStyle.GradientBorder clone() {
         return new BoxStyle.GradientBorder(
            JadeCodecs.nullableClone(this.boxProgressOffset),
            this.boxProgressColors,
            JadeCodecs.nullableClone(this.padding),
            this.bgColor,
            this.borderColor,
            this.borderWidth,
            Optional.ofNullable(this.roundCorner)
         );
      }

      @Override
      public boolean hasRoundCorner() {
         return this.roundCorner == null ? !IWailaConfig.get().getOverlay().getSquare() : this.roundCorner;
      }
   }

   public static class SpriteBase extends BoxStyle {
      public ResourceLocation sprite;
      @Nullable
      public ResourceLocation withIconSprite;

      public SpriteBase(
         Optional<float[]> boxProgressOffset,
         ColorPalette boxProgressColors,
         Optional<int[]> padding,
         ResourceLocation sprite,
         Optional<ResourceLocation> withIconSprite
      ) {
         super(boxProgressOffset, boxProgressColors, padding);
         this.sprite = sprite;
         this.withIconSprite = withIconSprite.orElse(null);
      }

      public static void blitSprite(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int i, int j, int k, int l, int m) {
         GuiSpriteManager sprites = Minecraft.getInstance().getGuiSprites();
         TextureAtlasSprite textureAtlasSprite = sprites.getSprite(resourceLocation);
         GuiSpriteScaling guiSpriteScaling = sprites.getSpriteScaling(textureAtlasSprite);
         switch (guiSpriteScaling) {
            case Stretch ignored:
               guiGraphics.blitSprite(resourceLocation, i, j, k, l, m);
               break;
            case Tile tile:
               guiGraphics.blitTiledSprite(textureAtlasSprite, i, j, k, l, m, 0, 0, tile.width(), tile.height(), tile.width(), tile.height());
               break;
            case NineSlice nineSlice:
               blitNineSlicedSprite(guiGraphics, textureAtlasSprite, nineSlice, i, j, k, l, m);
               break;
            default:
         }
      }

      public static void blitNineSlicedSprite(
         GuiGraphics guiGraphics, TextureAtlasSprite textureAtlasSprite, NineSlice nineSlice, int i, int j, int k, int l, int m
      ) {
         Border border = nineSlice.border();
         int n = Math.min(border.left(), l / 2);
         int o = Math.min(border.right(), l / 2);
         int p = Math.min(border.top(), m / 2);
         int q = Math.min(border.bottom(), m / 2);
         if (l == nineSlice.width() && m == nineSlice.height()) {
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, i, j, k, l, m);
         } else if (m == nineSlice.height()) {
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, i, j, k, n, m);
            guiGraphics.blitTiledSprite(
               textureAtlasSprite, i + n, j, k, l - o - n, m, n, 0, nineSlice.width() - o - n, nineSlice.height(), nineSlice.width(), nineSlice.height()
            );
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, 0, i + l - o, j, k, o, m);
         } else if (l == nineSlice.width()) {
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, i, j, k, l, p);
            guiGraphics.blitTiledSprite(
               textureAtlasSprite, i, j + p, k, l, m - q - p, 0, p, nineSlice.width(), nineSlice.height() - q - p, nineSlice.width(), nineSlice.height()
            );
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - q, i, j + m - q, k, l, q);
         } else {
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, i, j, k, n, p);
            guiGraphics.blitTiledSprite(
               textureAtlasSprite, i + n, j, k, l - o - n, p, n, 0, nineSlice.width() - o - n, p, nineSlice.width(), nineSlice.height()
            );
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, 0, i + l - o, j, k, o, p);
            guiGraphics.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - q, i, j + m - q, k, n, q);
            guiGraphics.blitTiledSprite(
               textureAtlasSprite,
               i + n,
               j + m - q,
               k,
               l - o - n,
               q,
               n,
               nineSlice.height() - q,
               nineSlice.width() - o - n,
               q,
               nineSlice.width(),
               nineSlice.height()
            );
            guiGraphics.blitSprite(
               textureAtlasSprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, nineSlice.height() - q, i + l - o, j + m - q, k, o, q
            );
            guiGraphics.blitTiledSprite(
               textureAtlasSprite, i, j + p, k, n, m - q - p, 0, p, n, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height()
            );
            guiGraphics.blitTiledSprite(
               textureAtlasSprite,
               i + n,
               j + p,
               k,
               l - o - n,
               m - q - p,
               n,
               p,
               nineSlice.width() - o - n,
               nineSlice.height() - q - p,
               nineSlice.width(),
               nineSlice.height()
            );
            guiGraphics.blitTiledSprite(
               textureAtlasSprite,
               i + l - o,
               j + p,
               k,
               o,
               m - q - p,
               nineSlice.width() - o,
               p,
               o,
               nineSlice.height() - q - p,
               nineSlice.width(),
               nineSlice.height()
            );
         }
      }

      @Override
      public void render(GuiGraphics guiGraphics, StyledElement element, float x, float y, float w, float h, float alpha) {
         ResourceLocation texture = this.sprite;
         if (this.withIconSprite != null && element.getIcon() != null) {
            texture = this.withIconSprite;
         }

         RenderSystem.enableBlend();
         guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
         blitSprite(guiGraphics, texture, Math.round(x), Math.round(y), 0, Math.round(w), Math.round(h));
         guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      @Override
      public float borderWidth() {
         return 0.0F;
      }

      public BoxStyle.SpriteBase clone() {
         return new BoxStyle.SpriteBase(
            JadeCodecs.nullableClone(this.boxProgressOffset),
            this.boxProgressColors,
            JadeCodecs.nullableClone(this.padding),
            this.sprite,
            Optional.ofNullable(this.withIconSprite)
         );
      }
   }
}
