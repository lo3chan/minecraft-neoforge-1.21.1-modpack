package net.mehvahdjukaar.moonlight.api.util.math.colors;

import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public class HCLVColor extends BaseColor<HCLVColor> {
   public HCLVColor(float h, float c, float l, float a) {
      super(h, c, l, a);
   }

   @Override
   public String toString() {
      return String.format("H: %s, C: %s, L %s", (int)(255.0F * this.hue()), (int)(255.0F * this.chroma()), (int)(255.0F * this.luminance()));
   }

   public float hue() {
      return this.v0;
   }

   public float chroma() {
      return this.v1;
   }

   public float luminance() {
      return this.v2;
   }

   public float alpha() {
      return this.v3;
   }

   public HCLVColor withHue(float hue) {
      return new HCLVColor(hue, this.chroma(), this.luminance(), this.alpha());
   }

   public HCLVColor withChroma(float chroma) {
      return new HCLVColor(this.hue(), chroma, this.luminance(), this.alpha());
   }

   public HCLVColor withLuminance(float luminance) {
      return new HCLVColor(this.hue(), this.chroma(), luminance, this.alpha());
   }

   public HCLVColor withAlpha(float alpha) {
      return new HCLVColor(this.hue(), this.chroma(), this.luminance(), alpha);
   }

   @Override
   public RGBColor asRGB() {
      return ColorSpaces.HCLVtoLUV(this).asRGB();
   }

   @Override
   public HCLVColor asHCLV() {
      return this;
   }

   public static HCLVColor averageColors(HCLVColor... colors) {
      float size = colors.length;
      Stream<Float> list = Arrays.stream(colors).map(HCLVColor::hue);
      Float[] hues = list.toArray(Float[]::new);
      float s = 0.0F;
      float v = 0.0F;
      float a = 0.0F;

      for (HCLVColor c : colors) {
         s += c.chroma();
         v += c.luminance();
         a += c.alpha();
      }

      return new HCLVColor(averageAngles(hues), s / size, v / size, a / size);
   }

   public HCLVColor multiply(float hue, float chroma, float luminance, float alpha) {
      return new HCLVColor(
         Mth.clamp(hue * this.hue(), 0.0F, 1.0F),
         Mth.clamp(chroma * this.chroma(), 0.0F, 1.0F),
         Mth.clamp(luminance * this.luminance(), 0.0F, 1.0F),
         Mth.clamp(alpha * this.alpha(), 0.0F, 1.0F)
      );
   }

   public HCLVColor mixWith(HCLVColor color, float bias) {
      float i = 1.0F - bias;
      float h = weightedAverageAngles(this.hue(), color.hue(), bias);

      while (h < 0.0F) {
         h++;
      }

      float c = this.chroma() * i + color.chroma() * bias;
      float b = this.luminance() * i + color.luminance() * bias;
      float a = this.alpha() * i + color.alpha() * bias;
      return new HCLVColor(h, c, b, a);
   }

   public HCLVColor fromRGB(RGBColor rgb) {
      return rgb.asHCLV();
   }

   public float distTo(HCLVColor other) {
      float h = this.hue();
      float h2 = other.hue();
      float c = this.chroma();
      float c2 = other.chroma();
      double x = c * Math.cos(h * 3.141592653589793 * 2.0) - c2 * Math.cos(h2 * 3.141592653589793 * 2.0);
      double y = c * Math.sin(h * 3.141592653589793 * 2.0) - c2 * Math.sin(h2 * 3.141592653589793 * 2.0);
      return (float)Math.sqrt(x * x + y * y + (this.luminance() - other.luminance()) * (this.luminance() - other.luminance()));
   }
}
