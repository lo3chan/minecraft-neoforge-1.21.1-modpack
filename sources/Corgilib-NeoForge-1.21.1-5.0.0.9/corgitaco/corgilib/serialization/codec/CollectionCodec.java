package corgitaco.corgilib.serialization.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public record CollectionCodec<TYPE, COLLECTION extends Collection<TYPE>>(Codec<TYPE> element, Supplier<COLLECTION> collectionConstruction)
   implements Codec<COLLECTION> {
   public <T> DataResult<T> encode(COLLECTION input, DynamicOps<T> ops, T prefix) {
      ListBuilder<T> builder = ops.listBuilder();

      for (TYPE type : input) {
         builder.add(this.element.encodeStart(ops, type));
      }

      return builder.build(prefix);
   }

   public <T> DataResult<Pair<COLLECTION, T>> decode(DynamicOps<T> ops, T input) {
      return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(consumer -> {
         COLLECTION result = this.collectionConstruction.get();
         List<T> errors = new ArrayList<>();
         consumer.accept(t -> {
            DataResult<Pair<TYPE, T>> decoded = this.element.decode(ops, t);
            decoded.error().ifPresent(e -> errors.add((T)t));
            result.add((TYPE)((Pair)decoded.getOrThrow()).getFirst());
         });
         return DataResult.success(Pair.of(result, ops.createList(errors.stream())));
      });
   }
}
