package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;

public record HsbColor(float hue, float saturation, float brightness, int alpha) {
   public static HsbColor of(float hue, float saturation, float brightness, int alpha) {
      return new HsbColor(hue, saturation, brightness, alpha);
   }

   public static HsbColor fromRgb(int rgba) {
      int r = ARGB32.red(rgba);
      int g = ARGB32.green(rgba);
      int b = ARGB32.blue(rgba);
      int a = ARGB32.alpha(rgba);
      int cmax = Math.max(Math.max(r, g), b);
      int cmin = Math.min(Math.min(r, g), b);
      float brightness = cmax / 255.0F;
      float saturation = cmax != 0 ? (float)(cmax - cmin) / cmax : 0.0F;
      float hue;
      if (saturation == 0.0F) {
         hue = 0.0F;
      } else {
         float redc = (float)(cmax - r) / (cmax - cmin);
         float greenc = (float)(cmax - g) / (cmax - cmin);
         float bluec = (float)(cmax - b) / (cmax - cmin);
         if (r == cmax) {
            hue = bluec - greenc;
         } else if (g == cmax) {
            hue = 2.0F + redc - bluec;
         } else {
            hue = 4.0F + greenc - redc;
         }

         hue /= 6.0F;
         if (hue < 0.0F) {
            hue++;
         }
      }

      return new HsbColor(hue, saturation, brightness, a);
   }

   public int toRgba() {
      return Mth.hsvToArgb(this.hue, this.saturation, this.brightness, this.alpha);
   }

   public HsbColor withAlpha(int alpha) {
      return new HsbColor(this.hue, this.saturation, this.brightness, alpha);
   }

   public HsbColor withBrightness(float brightness) {
      return new HsbColor(this.hue, this.saturation, brightness, this.alpha);
   }

   public HsbColor withSaturation(float saturation) {
      return new HsbColor(this.hue, saturation, this.brightness, this.alpha);
   }

   public HsbColor withHue(float hue) {
      return new HsbColor(hue, this.saturation, this.brightness, this.alpha);
   }
}
