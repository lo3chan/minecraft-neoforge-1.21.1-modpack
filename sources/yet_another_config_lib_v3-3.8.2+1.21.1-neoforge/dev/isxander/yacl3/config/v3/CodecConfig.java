package dev.isxander.yacl3.config.v3;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class CodecConfig<S extends CodecConfig<S>> implements EntryAddable, Codec<S> {
   private final List<ReadonlyConfigEntry<?>> entries = new ArrayList<>();

   @Override
   public <T> ConfigEntry<T> register(String fieldName, T defaultValue, Codec<T> codec) {
      ConfigEntry<T> entry = new CodecConfigEntryImpl<>(fieldName, defaultValue, codec);
      this.entries.add(entry);
      return entry;
   }

   @Override
   public <T extends CodecConfig<T>> ReadonlyConfigEntry<T> register(String fieldName, T configInstance) {
      ReadonlyConfigEntry<T> entry = new ChildConfigEntryImpl<>(fieldName, configInstance);
      this.entries.add(entry);
      return entry;
   }

   protected void onFinishedDecode(boolean successful) {
   }

   public <R> DataResult<R> encode(S input, DynamicOps<R> ops, R prefix) {
      if (input != null && input != this) {
         throw new IllegalArgumentException("`input` is ignored. It must be null or equal to `this`.");
      } else {
         return this.encode(ops, prefix);
      }
   }

   public <R> DataResult<Pair<S, R>> decode(DynamicOps<R> ops, R input) {
      this.decode(input, ops);
      return DataResult.success(Pair.of(this, input));
   }

   public final <R> DataResult<R> encode(DynamicOps<R> ops, R prefix) {
      RecordBuilder<R> builder = ops.mapBuilder();

      for (ReadonlyConfigEntry<?> entry : this.entries) {
         builder = entry.encode(ops, builder);
      }

      return builder.build(prefix);
   }

   public final <R> DataResult<R> encodeStart(DynamicOps<R> ops) {
      return this.encode(ops, (R)ops.empty());
   }

   public final <R> boolean decode(R encoded, DynamicOps<R> ops) {
      boolean success = true;

      for (ReadonlyConfigEntry<?> entry : this.entries) {
         success &= entry.decode(encoded, ops);
      }

      this.onFinishedDecode(success);
      return success;
   }
}
