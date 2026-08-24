package net.mehvahdjukaar.moonlight.api.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record AlternativeCodec<A>(Codec<? extends A>... codecs) implements Codec<A> {
   @SafeVarargs
   public AlternativeCodec(Codec<? extends A>... codecs) {
      this.codecs = codecs;
   }

   public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
      List<String> errors = new ArrayList<>();
      DataResult<Pair<A, T>> lastPartial = null;

      for (int i = 0; i < this.codecs.length; i++) {
         Codec<? extends A> codec = this.codecs[i];
         DataResult<? extends Pair<? extends A, T>> result = codec.decode(ops, input);
         if (result.isSuccess()) {
            return result.map(vo -> Pair.of(vo.getFirst(), vo.getSecond()));
         }

         if (result.hasResultOrPartial()) {
            lastPartial = result.map(vo -> Pair.of(vo.getFirst(), vo.getSecond()));
         }

         int finalI = i;
         result.error().ifPresent(e -> errors.add("[" + finalI + "]: " + e.message()));
      }

      if (lastPartial != null) {
         return lastPartial;
      } else {
         String combined = String.join("; ", errors);
         return DataResult.error(() -> "Failed to parse any alternative codec. Errors: " + combined);
      }
   }

   public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
      for (Codec<? extends A> codec : this.codecs) {
         try {
            DataResult<T> encoded = codec.encode(input, ops, prefix);
            if (encoded.isSuccess()) {
               return encoded;
            }
         } catch (ClassCastException var9) {
         }
      }

      return DataResult.error(() -> "No alternative codec could encode value: " + input);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("AlternativeCodec[");

      for (int i = 0; i < this.codecs.length; i++) {
         sb.append(this.codecs[i]);
         if (i < this.codecs.length - 1) {
            sb.append(", ");
         }
      }

      sb.append("]");
      return sb.toString();
   }
}
