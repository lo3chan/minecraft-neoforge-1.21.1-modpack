package dev.latvian.mods.kubejs.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record OrCodec<V>(List<Codec<V>> codecs) implements Codec<V> {
   public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
      for (int i = 0; i < this.codecs.size() - 1; i++) {
         DataResult<Pair<V, T>> result = this.codecs.get(i).decode(ops, input);
         if (result.error().isEmpty()) {
            return result;
         }
      }

      return ((Codec)this.codecs.getLast()).decode(ops, input);
   }

   public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
      for (int i = 0; i < this.codecs.size() - 1; i++) {
         DataResult<T> result = this.codecs.get(i).encode(input, ops, prefix);
         if (result.error().isEmpty()) {
            return result;
         }
      }

      return ((Codec)this.codecs.getLast()).encode(input, ops, prefix);
   }

   @NotNull
   @Override
   public String toString() {
      return "OrCodec" + this.codecs;
   }
}
