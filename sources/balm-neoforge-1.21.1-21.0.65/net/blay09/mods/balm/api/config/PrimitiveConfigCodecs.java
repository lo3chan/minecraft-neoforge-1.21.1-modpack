package net.blay09.mods.balm.api.config;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.blay09.mods.balm.api.config.schema.builder.BooleanConfigProperty;
import net.blay09.mods.balm.api.config.schema.builder.DoubleConfigProperty;
import net.blay09.mods.balm.api.config.schema.builder.FloatConfigProperty;
import net.blay09.mods.balm.api.config.schema.builder.IntConfigProperty;
import net.blay09.mods.balm.api.config.schema.builder.LongConfigProperty;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public class PrimitiveConfigCodecs {
   public static <T> Codec<T> codec(Class<T> type) {
      if (type == String.class) {
         return Codec.STRING;
      } else if (type == Integer.class || type == int.class) {
         return (Codec<T>)IntConfigProperty.CODEC;
      } else if (type == Long.class || type == long.class) {
         return (Codec<T>)LongConfigProperty.CODEC;
      } else if (type == Float.class || type == float.class) {
         return (Codec<T>)FloatConfigProperty.CODEC;
      } else if (type == Double.class || type == double.class) {
         return (Codec<T>)DoubleConfigProperty.CODEC;
      } else if (type == Boolean.class || type == boolean.class) {
         return (Codec<T>)BooleanConfigProperty.CODEC;
      } else if (type == ResourceLocation.class) {
         return ResourceLocation.CODEC;
      } else if (type.isEnum()) {
         return enumCodec(type);
      } else {
         throw new IllegalArgumentException("Unsupported nested type: " + type.getName());
      }
   }

   private static <T extends Enum<T>> Codec<T> enumCodec(Class<T> type) {
      return LenientEnumCodecs.fromValues(type::getEnumConstants);
   }

   public static <T> StreamCodec<ByteBuf, T> streamCodec(Class<T> type) {
      if (type == String.class) {
         return ByteBufCodecs.STRING_UTF8;
      } else if (type == Integer.class || type == int.class) {
         return ByteBufCodecs.INT;
      } else if (type == Long.class || type == long.class) {
         return ByteBufCodecs.VAR_LONG;
      } else if (type == Float.class || type == float.class) {
         return ByteBufCodecs.FLOAT;
      } else if (type == Double.class || type == double.class) {
         return ByteBufCodecs.DOUBLE;
      } else if (type == Boolean.class || type == boolean.class) {
         return ByteBufCodecs.BOOL;
      } else if (type == ResourceLocation.class) {
         return ResourceLocation.STREAM_CODEC;
      } else if (type.isEnum()) {
         return enumStreamCodec(type);
      } else {
         throw new IllegalArgumentException("Unsupported nested type: " + type.getName());
      }
   }

   private static <T extends Enum<T>> StreamCodec<ByteBuf, T> enumStreamCodec(Class<T> type) {
      IntFunction<T> byIdMapper = ByIdMap.continuous(Enum::ordinal, type.getEnumConstants(), OutOfBoundsStrategy.ZERO);
      return ByteBufCodecs.idMapper(byIdMapper, Enum::ordinal);
   }
}
