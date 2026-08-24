package corgitaco.corgilib.serialization.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Optional;

public record FromFileCodec<E>(Codec<E> elementCodec, String internalKey) implements Codec<Wrapped<E>> {
   public static <E> FromFileCodec<E> create(Codec<E> codec, String internalKey) {
      return new FromFileCodec<>(codec, internalKey);
   }

   public <T> DataResult<Pair<Wrapped<E>, T>> decode(DynamicOps<T> ops, T input) {
      if (ops instanceof FromFileOps<T> ops1) {
         Map<String, E> registry = ops1.getAccess(this.internalKey);
         DataResult<Pair<String, T>> id = Codec.STRING.decode(ops, input);
         if (id.result().isEmpty()) {
            return this.elementCodec.decode(ops, input).map(m -> m.mapFirst(ex -> new Wrapped<>(Optional.empty(), ex)));
         } else {
            Pair<String, T> stringTPair = (Pair<String, T>)id.result().get();
            String key = (String)stringTPair.getFirst();
            if (!registry.containsKey(key)) {
               return DataResult.error(() -> String.format("\"%s\" does not exist", key));
            } else {
               E e = registry.get(key);
               return DataResult.success(Pair.of(e instanceof Wrapped wrapped ? wrapped : new Wrapped<>(Optional.of(key), e), stringTPair.getSecond()));
            }
         }
      } else {
         return this.elementCodec.decode(ops, input).map(etPair -> Pair.of(new Wrapped<>(Optional.empty(), etPair.getFirst()), etPair.getSecond()));
      }
   }

   public <T> DataResult<T> encode(Wrapped<E> input, DynamicOps<T> ops, T prefix) {
      Optional<String> id = input.id();
      return ops instanceof FromFileOps<T> fromFileOps && id.isPresent() && fromFileOps.getAccess(this.internalKey).containsKey(id.get())
         ? Codec.STRING.encode(id.get(), ops, prefix)
         : this.elementCodec.encode(input.value(), ops, prefix);
   }
}
