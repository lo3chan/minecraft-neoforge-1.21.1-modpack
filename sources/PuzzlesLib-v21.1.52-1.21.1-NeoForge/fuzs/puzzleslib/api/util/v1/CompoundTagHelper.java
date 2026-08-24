package fuzs.puzzleslib.api.util.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public final class CompoundTagHelper {
   private CompoundTagHelper() {
   }

   public static <T> void store(CompoundTag compoundTag, String key, Codec<T> codec, T data) {
      store(compoundTag, key, codec, NbtOps.INSTANCE, data);
   }

   public static <T> void storeNullable(CompoundTag compoundTag, String key, Codec<T> codec, @Nullable T data) {
      if (data != null) {
         store(compoundTag, key, codec, data);
      }
   }

   public static <T> void store(CompoundTag compoundTag, String key, Codec<T> codec, DynamicOps<Tag> ops, T data) {
      compoundTag.put(key, (Tag)codec.encodeStart(ops, data).getOrThrow());
   }

   public static <T> void storeNullable(CompoundTag compoundTag, String key, Codec<T> codec, DynamicOps<Tag> ops, @Nullable T data) {
      if (data != null) {
         store(compoundTag, key, codec, ops, data);
      }
   }

   public static <T> void store(CompoundTag compoundTag, MapCodec<T> mapCodec, T data) {
      store(compoundTag, mapCodec, NbtOps.INSTANCE, data);
   }

   public static <T> void store(CompoundTag compoundTag, MapCodec<T> mapCodec, DynamicOps<Tag> ops, T data) {
      compoundTag.merge((CompoundTag)mapCodec.encoder().encodeStart(ops, data).getOrThrow());
   }

   public static <T> Optional<T> read(CompoundTag compoundTag, String key, Codec<T> codec) {
      return read(compoundTag, key, codec, NbtOps.INSTANCE);
   }

   public static <T> Optional<T> read(CompoundTag compoundTag, String key, Codec<T> codec, DynamicOps<Tag> ops) {
      Tag tag = compoundTag.get(key);
      return tag == null
         ? Optional.empty()
         : codec.parse(ops, tag).resultOrPartial(string -> PuzzlesLib.LOGGER.error("Failed to read field ({}={}): {}", new Object[]{key, tag, string}));
   }

   public static <T> Optional<T> read(CompoundTag compoundTag, MapCodec<T> mapCodec) {
      return read(compoundTag, mapCodec, NbtOps.INSTANCE);
   }

   public static <T> Optional<T> read(CompoundTag compoundTag, MapCodec<T> mapCodec, DynamicOps<Tag> ops) {
      return mapCodec.decode(ops, (MapLike)ops.getMap(compoundTag).getOrThrow())
         .resultOrPartial(string -> PuzzlesLib.LOGGER.error("Failed to read value ({}): {}", compoundTag, string));
   }
}
