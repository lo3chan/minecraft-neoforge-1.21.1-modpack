package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FluteData(int commandSelected, int helpCommandSelected, int commandMode, List<FluteData.CrowIds> crowList, int dyeColor1, int dyeColor2) {
   public static final FluteData EMPTY = new FluteData(0, 0, 0, new ArrayList<>(), 0, 0);
   public static final Codec<FluteData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("commandSelected").forGetter(FluteData::commandSelected),
            Codec.INT.fieldOf("helpCommandSelected").forGetter(FluteData::helpCommandSelected),
            Codec.INT.fieldOf("commandMode").forGetter(FluteData::commandMode),
            FluteData.CrowIds.CODEC.listOf().fieldOf("crowList").forGetter(FluteData::crowList),
            Codec.INT.fieldOf("dyeColor1").forGetter(FluteData::dyeColor1),
            Codec.INT.fieldOf("dyeColor2").forGetter(FluteData::dyeColor2)
         )
         .apply(instance, FluteData::new)
   );
   public static StreamCodec<ByteBuf, FluteData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

   public static FluteData empty() {
      return new FluteData(0, 0, 0, new ArrayList<>(), 0, 0);
   }

   public record CrowIds(UUID uuid, int id) {
      public static final Codec<FluteData.CrowIds> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(UUIDUtil.CODEC.fieldOf("uuid").forGetter(FluteData.CrowIds::uuid), Codec.INT.fieldOf("id").forGetter(FluteData.CrowIds::id))
            .apply(instance, FluteData.CrowIds::new)
      );
   }
}
