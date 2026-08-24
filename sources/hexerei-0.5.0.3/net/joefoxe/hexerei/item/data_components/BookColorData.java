package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BookColorData(int color1, int color2) {
   public static int DEFAULT_1 = 12686147;
   public static int DEFAULT_2 = 11030626;
   public static final BookColorData EMPTY = new BookColorData(DEFAULT_1, DEFAULT_2);
   public static final BookColorData EMPTY_NOTEBOOK = new BookColorData(7240066, 15790320);
   public static final BookColorData EMPTY_COLORS = new BookColorData(7240066, 42389);
   public static final Codec<BookColorData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.fieldOf("color1").forGetter(BookColorData::color1), Codec.INT.fieldOf("color2").forGetter(BookColorData::color2))
         .apply(instance, BookColorData::new)
   );
   public static StreamCodec<ByteBuf, BookColorData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
