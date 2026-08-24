package snownee.jade.impl.ui;

import com.google.common.base.Preconditions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Color;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;

public class SimpleProgressStyle extends ProgressStyle {
   public boolean autoTextColor = true;
   public int color;
   public int color2;
   public int textColor;
   public boolean vertical;

   public SimpleProgressStyle() {
      this.color(-1);
   }

   private static Vector3f RGBtoHSV(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      int max = Math.max(r, Math.max(g, b));
      int min = Math.min(r, Math.min(g, b));
      float v = max;
      float delta = max - min;
      if (max != 0) {
         float s = delta / max;
         float h;
         if (r == max) {
            h = (g - b) / delta;
         } else if (g == max) {
            h = 2.0F + (b - r) / delta;
         } else {
            h = 4.0F + (r - g) / delta;
         }

         h /= 6.0F;
         if (h < 0.0F) {
            h++;
         }

         return new Vector3f(h, s, v / 255.0F);
      } else {
         float sx = 0.0F;
         float hx = -1.0F;
         return new Vector3f(hx, sx, 0.0F);
      }
   }

   @Override
   public ProgressStyle color(int color, int color2) {
      this.color = color;
      this.color2 = color2;
      return this;
   }

   @Override
   public ProgressStyle direction(ScreenDirection direction) {
      Preconditions.checkArgument(direction == ScreenDirection.UP || direction == ScreenDirection.RIGHT, "Only UP and RIGHT are supported");
      super.direction(direction);
      this.vertical = direction.isVertical();
      return this;
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float width, float height, float progress, Component text) {
      progress *= this.choose(true, width, height);
      float progressY = y;
      if (this.vertical) {
         progressY = y + (height - progress);
      }

      if (progress > 0.0F) {
         if (this.overlay != null) {
            Vec2 size = new Vec2(this.choose(true, progress, width), this.choose(false, progress, height));
            this.overlay.size(size);
            this.overlay.render(guiGraphics, x, progressY, size.x, size.y);
         } else {
            Color color3 = Color.rgb(this.color);
            int lighter = Color.hsl(color3.getHue(), color3.getSaturation(), color3.getLightness() * 0.699999988079071, color3.getOpacity()).toInt();
            float half = this.choose(true, height, width) / 2.0F;
            DisplayHelper.INSTANCE
               .drawGradientRect(
                  guiGraphics, x, progressY, this.choose(true, progress, half), this.choose(false, progress, half), lighter, this.color, this.vertical
               );
            DisplayHelper.INSTANCE
               .drawGradientRect(
                  guiGraphics,
                  x + this.choose(false, half, 0.0F),
                  progressY + this.choose(true, half, 0.0F),
                  this.choose(true, progress, half),
                  this.choose(false, progress, half),
                  this.color,
                  lighter,
                  this.vertical
               );
            if (this.color != this.color2) {
               if (this.vertical) {
                  for (float yy = y + height; yy > progressY; yy -= 2.0F) {
                     float fy = Math.max(progressY, yy + 1.0F);
                     DisplayHelper.fill(guiGraphics, x, yy, x + width, fy, this.color2);
                  }
               } else {
                  for (float xx = x + 1.0F; xx < x + progress; xx += 2.0F) {
                     float fx = Math.min(x + width, xx + 1.0F);
                     DisplayHelper.fill(guiGraphics, xx, y, fx, y + height, this.color2);
                  }
               }
            }
         }
      }

      if (text != null) {
         Font font = DisplayHelper.font();
         if (this.autoTextColor) {
            this.autoTextColor = false;
            if (this.overlay == null && RGBtoHSV(this.color2).z() > 0.75F) {
               this.textColor = -16777216;
            } else {
               this.textColor = IThemeHelper.get().getNormalColor();
            }
         } else if (this.textColor == -1) {
            this.textColor = IThemeHelper.get().getNormalColor();
         }

         y += height - 9.0F;
         if (this.vertical && 9.0F < progress) {
            y -= progress;
            y += 9 + 2;
         }

         int color = IWailaConfig.IConfigOverlay.applyAlpha(this.textColor, OverlayRenderer.alpha);
         guiGraphics.drawString(font, text, (int)x + 1, (int)y, color);
      }
   }

   private float choose(boolean expand, float x, float y) {
      return this.vertical ^ expand ? x : y;
   }

   @Override
   public ProgressStyle textColor(int color) {
      this.textColor = color;
      this.autoTextColor = false;
      return this;
   }
}
