package dev.corgitaco.enhancedcelestials2core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ColorUtil {
   private static final long BIT_MASK = 255L;
   public static final Codec<Integer> NAMED_OR_HEX_CODEC = Codec.STRING.comapFlatMap(ColorUtil::parseNamedOrHex, ColorUtil::toHexString);
   public static final Codec<Integer> RGB_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.intRange(0, 255).fieldOf("r").forGetter(color -> color >> 16 & 0xFF),
            Codec.intRange(0, 255).fieldOf("g").forGetter(color -> color >> 8 & 0xFF),
            Codec.intRange(0, 255).fieldOf("b").forGetter(color -> color & 0xFF)
         )
         .apply(builder, (r, g, b) -> r << 16 | g << 8 | b)
   );
   public static final Codec<Integer> FLEXIBLE_COLOR_CODEC = Codec.withAlternative(NAMED_OR_HEX_CODEC, RGB_CODEC);
   public static final MapCodec<Integer> COLOR_CODEC = FLEXIBLE_COLOR_CODEC.fieldOf("color");

   private static DataResult<Integer> parseNamedOrHex(String input) {
      ChatFormatting chatFormatting = ChatFormatting.getByName(input);
      if (chatFormatting != null && chatFormatting.getColor() != null) {
         return DataResult.success(chatFormatting.getColor());
      } else {
         int parsed = tryParseColor(input);
         return parsed == 2147483647
            ? DataResult.error(() -> "\"" + input + "\" is not a valid color; expected a color name or hex code")
            : DataResult.success(parsed);
      }
   }

   public static int tryParseColor(String input) {
      int result = 2147483647;
      if (input.isEmpty()) {
         return result;
      } else {
         try {
            result = (int)Long.parseLong(input.replace("#", "").replace("0x", ""), 16);
         } catch (NumberFormatException var3) {
            var3.printStackTrace();
         }

         return result;
      }
   }

   public static String toHexString(int color) {
      return color == 2147483647 ? "ffffff" : Integer.toHexString(color);
   }

   private static int clamp(int target, int fallback) {
      return target == 2147483647 ? fallback : target;
   }

   public static int mix(int[] start, int[] end, double blend) {
      return pack(lerp(start[0], end[0], blend), lerp(start[1], end[1], blend), lerp(start[2], end[2], blend), lerp(start[3], end[3], blend));
   }

   public static int[] transformFloatColor(Vec3 floatColor) {
      return new int[]{255, (int)(floatColor.x() * 255.0), (int)(floatColor.y() * 255.0), (int)(floatColor.z() * 255.0)};
   }

   public static Vector3f glColor(int[] packedColor) {
      float r = packedColor[1] / 255.0F;
      float g = packedColor[2] / 255.0F;
      float b = packedColor[3] / 255.0F;
      return new Vector3f(r, g, b);
   }

   private static int lerp(int start, int end, double blend) {
      return (int)(start + (end - start) * blend);
   }

   public static int pack(int a, int r, int g, int b) {
      return (int)((a & 255L) << 24 | (r & 255L) << 16 | (g & 255L) << 8 | b & 255L);
   }

   public static int pack(int r, int g, int b) {
      return pack(255, r, g, b);
   }

   public static int[] unpack(int decimal) {
      return new int[]{(int)(decimal >> 24 & 255L), (int)(decimal >> 16 & 255L), (int)(decimal >> 8 & 255L), (int)(decimal & 255L)};
   }
}
