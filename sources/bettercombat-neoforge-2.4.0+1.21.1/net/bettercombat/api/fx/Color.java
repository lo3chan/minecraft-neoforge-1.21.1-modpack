package net.bettercombat.api.fx;

public record Color(float red, float green, float blue, float alpha) {
   public static final Color RED = new Color(1.0F, 0.0F, 0.0F);
   public static final Color GREEN = new Color(0.0F, 1.0F, 0.0F);
   public static final Color BLUE = new Color(0.0F, 0.0F, 1.0F);
   public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F);

   public Color(float red, float green, float blue) {
      this(red, green, blue, 1.0F);
   }

   public Color alpha(float alpha) {
      return new Color(this.red, this.green, this.blue, alpha);
   }

   public static Color from(int rgb) {
      float red = (rgb >> 16 & 0xFF) / 255.0F;
      float green = (rgb >> 8 & 0xFF) / 255.0F;
      float blue = (rgb & 0xFF) / 255.0F;
      return new Color(red, green, blue);
   }

   public static Color fromRGBA(long rgba) {
      float red = (float)(rgba >> 24 & 255L) / 255.0F;
      float green = (float)(rgba >> 16 & 255L) / 255.0F;
      float blue = (float)(rgba >> 8 & 255L) / 255.0F;
      float alpha = (float)(rgba & 255L) / 255.0F;
      return new Color(red, green, blue, alpha);
   }

   public static Color fromStringRGB(String rgbString) {
      if (rgbString.startsWith("#")) {
         rgbString = rgbString.substring(1);
      }

      if (rgbString.length() != 6) {
         throw new IllegalArgumentException("Invalid RGB string format");
      } else {
         int rgb = Integer.parseInt(rgbString, 16);
         return from(rgb);
      }
   }

   public Color blend(Color other, float ratio) {
      return blend(this, other, ratio);
   }

   public static Color blend(Color color1, Color color2, float ratio) {
      float red = color1.red * (1.0F - ratio) + color2.red * ratio;
      float green = color1.green * (1.0F - ratio) + color2.green * ratio;
      float blue = color1.blue * (1.0F - ratio) + color2.blue * ratio;
      float alpha = color1.alpha * (1.0F - ratio) + color2.alpha * ratio;
      return new Color(red, green, blue, alpha);
   }

   public Color.IntFormat toIntFormat() {
      return new Color.IntFormat((int)(this.red * 255.0F), (int)(this.green * 255.0F), (int)(this.blue * 255.0F), (int)(this.alpha * 255.0F));
   }

   public Color.ByteFormat toByteFormat() {
      return new Color.ByteFormat((byte)(this.red * 255.0F), (byte)(this.green * 255.0F), (byte)(this.blue * 255.0F), (byte)(this.alpha * 255.0F));
   }

   public long toRGBA() {
      return (long)(this.red * 255.0F) << 24 | (long)(this.green * 255.0F) << 16 | (long)(this.blue * 255.0F) << 8 | (long)(this.alpha * 255.0F);
   }

   public long toARGB() {
      return (long)(this.alpha * 255.0F) << 24 | (long)(this.red * 255.0F) << 16 | (long)(this.green * 255.0F) << 8 | (long)(this.blue * 255.0F);
   }

   public String toStringRGB() {
      return String.format("#%02X%02X%02X", (int)(this.red * 255.0F), (int)(this.green * 255.0F), (int)(this.blue * 255.0F));
   }

   public record ByteFormat(byte red, byte green, byte blue, byte alpha) {
   }

   public record IntFormat(int red, int green, int blue, int alpha) {
      public static Color.IntFormat fromLongRGBA(long rgba) {
         long red = rgba >> 24 & 255L;
         long green = rgba >> 16 & 255L;
         long blue = rgba >> 8 & 255L;
         long alpha = rgba & 255L;
         return new Color.IntFormat((int)red, (int)green, (int)blue, (int)alpha);
      }
   }
}
