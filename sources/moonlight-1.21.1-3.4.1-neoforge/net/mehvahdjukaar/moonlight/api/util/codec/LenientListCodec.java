package net.mehvahdjukaar.moonlight.api.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.ArrayList;
import java.util.List;

public record LenientListCodec<E>(Codec<E> elementCodec) implements Codec<List<E>> {
   public static <A> LenientListCodec<A> of(Codec<A> elementCodec) {
      return new LenientListCodec((Codec<E>)elementCodec);
   }

   public <T> DataResult<T> encode(List<E> input, DynamicOps<T> ops, T prefix) {
      ListBuilder<T> builder = ops.listBuilder();

      for (E element : input) {
         builder.add(this.elementCodec.encodeStart(ops, element));
      }

      return builder.build(prefix);
   }

   public <T> DataResult<Pair<List<E>, T>> decode(DynamicOps<T> ops, T input) {
      return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(listOps -> {
         List<E> elements = new ArrayList<>();
         listOps.accept(value -> {
            DataResult<Pair<E, T>> elementResult = this.elementCodec.decode(ops, value);
            elementResult.map(p -> elements.add((E)p.getFirst()));
         });
         return DataResult.success(Pair.of(List.copyOf(elements), input), Lifecycle.stable());
      });
   }

   @Override
   public String toString() {
      return "LenientListCodec[" + this.elementCodec + "]";
   }
}
