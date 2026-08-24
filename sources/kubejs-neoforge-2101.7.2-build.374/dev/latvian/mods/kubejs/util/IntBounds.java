package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record IntBounds(int min, int max) {
   public static final IntBounds DEFAULT = new IntBounds(1, 2147483647);
   public static final IntBounds OPTIONAL = new IntBounds(0, 2147483647);
   public static final MapCodec<IntBounds> MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min", 1).forGetter(IntBounds::min),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("max", 2147483647).forGetter(IntBounds::max)
         )
         .apply(instance, IntBounds::of)
   );
   public static final Codec<IntBounds> CODEC = MAP_CODEC.codec();
   public static final StreamCodec<ByteBuf, IntBounds> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, IntBounds::min, ByteBufCodecs.VAR_INT, IntBounds::max, IntBounds::of
   );

   public static IntBounds of(int min, int max) {
      if (max == 2147483647) {
         if (min == 1) {
            return DEFAULT;
         }

         if (min == 0) {
            return OPTIONAL;
         }
      }

      return new IntBounds(min, max);
   }
}
