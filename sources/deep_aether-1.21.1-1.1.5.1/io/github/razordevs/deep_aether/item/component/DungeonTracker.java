package io.github.razordevs.deep_aether.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DungeonTracker(Optional<GlobalPos> target, boolean found) {
   public static final Codec<DungeonTracker> CODEC = RecordCodecBuilder.create(
      record -> record.group(
            GlobalPos.CODEC.optionalFieldOf("target").forGetter(DungeonTracker::target),
            Codec.BOOL.optionalFieldOf("tracked", Boolean.TRUE).forGetter(DungeonTracker::found)
         )
         .apply(record, DungeonTracker::new)
   );
   public static final StreamCodec<ByteBuf, DungeonTracker> STREAM_CODEC = StreamCodec.composite(
      GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), DungeonTracker::target, ByteBufCodecs.BOOL, DungeonTracker::found, DungeonTracker::new
   );
}
