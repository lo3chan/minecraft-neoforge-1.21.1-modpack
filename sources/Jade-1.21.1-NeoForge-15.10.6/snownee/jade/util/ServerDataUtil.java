package snownee.jade.util;

import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class ServerDataUtil {
   public static <T> Optional<T> read(CompoundTag data, MapDecoder<T> codec) {
      MapLike<Tag> mapLike = (MapLike<Tag>)NbtOps.INSTANCE.getMap(data).getOrThrow();
      return codec.decode(NbtOps.INSTANCE, mapLike).result();
   }

   public static <T> void write(CompoundTag data, MapEncoder<T> codec, T value) {
      Tag tag = (Tag)codec.encode(value, NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder()).build(new CompoundTag()).result().orElseThrow();
      data.merge((CompoundTag)tag);
   }
}
