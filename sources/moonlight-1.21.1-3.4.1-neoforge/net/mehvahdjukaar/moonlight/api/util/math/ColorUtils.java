package net.mehvahdjukaar.moonlight.api.util.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;
import net.mehvahdjukaar.codecui.SchemaCodecs;
import net.mehvahdjukaar.moonlight.api.util.math.colors.HSVColor;
import net.mehvahdjukaar.moonlight.api.util.math.colors.RGBColor;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor.ABGR32;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Vector3f;

public final class ColorUtils {
   public static final Codec<Integer> CODEC = SchemaCodecs.colorArgb(hexOrIntCodec(true));
   public static final Codec<Integer> RGB_CODEC = SchemaCodecs.colorRgb(hexOrIntCodec(false).xmap(i -> i & 16777215, i -> i & 16777215));
   private static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
   private static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, 1.0F, 0.7F).normalize();
   public static final float MINECRAFT_LIGHT_POWER = 0.6F;
   public static final float MINECRAFT_AMBIENT_LIGHT = 0.4F;

   public static Codec<Integer> codec(boolean hasAlpha) {
      return hasAlpha ? CODEC : RGB_CODEC;
   }

   private static Codec<Integer> hexOrIntCodec(boolean hasAlpha) {
      return Codec.either(Codec.INT, Codec.STRING.flatXmap(ColorUtils::isValidStringOrError, s -> isValidStringOrError(s).map(ColorUtils::formatString)))
         .xmap(either -> (Integer)either.map(integer -> integer, s -> Integer.parseUnsignedInt(s, 16)), integer -> Either.right(toHexString(integer, hasAlpha)));
   }

   private static String formatString(String s) {
      return "#" + s.toUpperCase(Locale.ROOT);
   }

   public static DataResult<String> isValidStringOrError(String s) {
      String st = s;
      if (s.startsWith("0x")) {
         st = s.substring(2);
      } else if (s.startsWith("#")) {
         st = s.substring(1);
      }

      if (st.length() > 8) {
         return DataResult.error(() -> "Invalid color format. Hex value must have up to 8 characters.");
      } else {
         try {
            int parsedValue = Integer.parseUnsignedInt(st, 16);
            return DataResult.success(st);
         } catch (NumberFormatException var3) {
            return DataResult.error(() -> "Invalid color format. Must be in ARGB hex format ('0xff00ff00', '#ff00ff00', 'ff00ff00') or its Integer value");
         }
      }
   }

   public static boolean isValidString(String s) {
      return isValidStringOrError(s).result().isPresent();
   }

   public static int parseHex(String s) {
      return Integer.parseUnsignedInt((String)isValidStringOrError(s).getOrThrow(), 16);
   }

   public static String toHexString(int argb) {
      return "#" + String.format("%08X", argb);
   }

   public static String toHexString(int color, boolean hasAlpha) {
      return hasAlpha ? "#" + String.format("%08X", color) : "#" + String.format("%06X", color & 16777215);
   }

   public static int hsvToArgb(float hue, float saturation, float value, int alpha) {
      return swapFormat(new HSVColor(hue, saturation, value, alpha / 255.0F).asRGB().toInt());
   }

   public static float[] argbToHsv(int argb) {
      HSVColor hsv = new RGBColor(swapFormat(argb)).asHSV();
      return new float[]{hsv.hue(), hsv.saturation(), hsv.value()};
   }

   public static int shadeColor(Vector3f normal, int color) {
      return multiply(color, getShading(normal));
   }

   public static float getShading(Vector3f normal) {
      if (normal.equals(Direction.UP.step())) {
         return 1.0F;
      } else {
         Vector3f lightDir0 = DIFFUSE_LIGHT_0;
         Vector3f lightDir1 = DIFFUSE_LIGHT_1;
         lightDir0.normalize();
         lightDir1.normalize();
         float light0 = Math.max(0.0F, lightDir0.dot(normal));
         float light1 = Math.max(0.0F, lightDir1.dot(normal));
         return Math.min(1.0F, (light0 + light1) * 0.6F + 0.4F);
      }
   }

   public static int multiply(int color, float amount) {
      if (amount == 1.0F) {
         return color;
      } else {
         int j = Math.min(255, (int)(ABGR32.red(color) * amount));
         int k = Math.min(255, (int)(ABGR32.green(color) * amount));
         int l = Math.min(255, (int)(ABGR32.blue(color) * amount));
         return ABGR32.color(0, l, k, j);
      }
   }

   public static int lerp(int c0, int c1, float t) {
      if (t == 0.0F) {
         return c0;
      } else if (t == 1.0F) {
         return c1;
      } else {
         RGBColor col = new RGBColor(c0);
         return col.mixWith(new RGBColor(c1), t).toInt();
      }
   }

   public static int swapFormat(int argb) {
      return argb & -16711936 | argb >> 16 & 0xFF | argb << 16 & 0xFF0000;
   }

   public static int pack(float[] rgb) {
      return ARGB32.color(255, (int)(rgb[0] * 255.0F), (int)(rgb[1] * 255.0F), (int)(rgb[2] * 255.0F));
   }

   public static float[] unpack(int color) {
      return new float[]{ABGR32.red(color) / 255.0F, ABGR32.green(color) / 255.0F, ABGR32.blue(color) / 255.0F};
   }
}
