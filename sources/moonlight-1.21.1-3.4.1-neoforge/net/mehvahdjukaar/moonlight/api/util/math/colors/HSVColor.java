package net.mehvahdjukaar.moonlight.api.util.math.colors;

import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public class HSVColor extends BaseColor<HSVColor> {
   public HSVColor(float h, float s, float b, float a) {
      super(h, s, b, a);
   }

   @Override
   public String toString() {
      return String.format("H: %s, S: %s, V %s", (int)(255.0F * this.hue()), (int)(255.0F * this.saturation()), (int)(255.0F * this.value()));
   }

   public float hue() {
      return this.v0;
   }

   public float saturation() {
      return this.v1;
   }

   public float value() {
      return this.v2;
   }

   public float alpha() {
      return this.v3;
   }

   public HSVColor withHue(float hue) {
      return new HSVColor(hue, this.saturation(), this.value(), this.alpha());
   }

   public HSVColor withSaturation(float saturation) {
      return new HSVColor(this.hue(), saturation, this.value(), this.alpha());
   }

   public HSVColor withValue(float value) {
      return new HSVColor(this.hue(), this.saturation(), value, this.alpha());
   }

   public HSVColor withAlpha(float alpha) {
      return new HSVColor(this.hue(), this.saturation(), this.value(), alpha);
   }

   @Override
   public HSVColor asHSV() {
      return this;
   }

   @Override
   public RGBColor asRGB() {
      return ColorSpaces.HSVtoRGB(this);
   }

   public static HSVColor averageColors(HSVColor... colors) {
      float size = colors.length;
      Stream<Float> list = Arrays.stream(colors).map(HSVColor::hue);
      Float[] hues = list.toArray(Float[]::new);
      float s = 0.0F;
      float v = 0.0F;
      float a = 0.0F;

      for (HSVColor c : colors) {
         s += c.saturation();
         v += c.value();
         a += c.alpha();
      }

      return new HSVColor(averageAngles(hues), s / size, v / size, a / size);
   }

   public HSVColor multiply(float hue, float saturation, float value, float alpha) {
      return new HSVColor(
         Mth.clamp(hue * this.hue(), 0.0F, 1.0F),
         Mth.clamp(saturation * this.saturation(), 0.0F, 1.0F),
         Mth.clamp(value * this.value(), 0.0F, 1.0F),
         Mth.clamp(alpha * this.alpha(), 0.0F, 1.0F)
      );
   }

   public HSVColor mixWith(HSVColor color, float bias) {
      float i = 1.0F - bias;
      float h = weightedAverageAngles(this.hue(), color.hue(), bias);

      while (h < 0.0F) {
         h++;
      }

      float s = this.saturation() * i + color.saturation() * bias;
      float v = this.value() * i + color.value() * bias;
      float a = this.alpha() * i + color.alpha() * bias;
      return new HSVColor(h, s, v, a);
   }

   public HSVColor fromRGB(RGBColor rgb) {
      return rgb.asHSV();
   }

   public float distTo(HSVColor other) {
      float h = this.hue();
      float h2 = other.hue();
      float c = this.saturation();
      float c2 = other.saturation();
      double x = c * Math.cos(h * 3.141592653589793 * 2.0) - c2 * Math.cos(h2 * 3.141592653589793 * 2.0);
      double y = c * Math.sin(h * 3.141592653589793 * 2.0) - c2 * Math.sin(h2 * 3.141592653589793 * 2.0);
      return (float)Math.sqrt(x * x + y * y + (this.value() - other.value()) * (this.value() - other.value()));
   }
}
