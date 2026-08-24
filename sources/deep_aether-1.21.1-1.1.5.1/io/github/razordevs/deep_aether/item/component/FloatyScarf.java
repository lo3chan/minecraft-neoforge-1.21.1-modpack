package io.github.razordevs.deep_aether.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FloatyScarf(int uuid, List<Integer> colors, byte currentModification) {
   public static final Codec<FloatyScarf> CODEC = RecordCodecBuilder.create(
      codec -> codec.group(
            Codec.INT.fieldOf("id").forGetter(FloatyScarf::uuid),
            Codec.INT.listOf().fieldOf("colors").forGetter(FloatyScarf::colors),
            Codec.BYTE.fieldOf("current").forGetter(FloatyScarf::currentModification)
         )
         .apply(codec, FloatyScarf::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FloatyScarf> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      FloatyScarf::uuid,
      ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
      FloatyScarf::colors,
      ByteBufCodecs.BYTE,
      FloatyScarf::currentModification,
      FloatyScarf::new
   );

   public static FloatyScarf withDefaultColor(int id) {
      List<Integer> list = new ArrayList<>();
      list.add(-1);
      list.add(-1);
      list.add(-1);
      list.add(-1);
      list.add(-1);
      return new FloatyScarf(id, list, (byte)0);
   }

   public List<Integer> colors() {
      return new ArrayList<>(this.colors);
   }
}
