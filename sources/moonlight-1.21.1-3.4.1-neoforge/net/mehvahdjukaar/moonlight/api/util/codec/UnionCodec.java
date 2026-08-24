package net.mehvahdjukaar.moonlight.api.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.BiFunction;

class UnionCodec<A, B> implements Codec<A> {
   private final Codec<A> codec;
   private final Codec<B> otherType;
   private final BiFunction<A, B, A> applyFunc;

   public UnionCodec(Codec<A> codec, Codec<B> otherType, BiFunction<A, B, A> applyFunc) {
      this.codec = codec;
      this.otherType = otherType;
      this.applyFunc = applyFunc;
   }

   public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
      DataResult<Pair<A, T>> original = this.codec.decode(ops, input);
      DataResult<Pair<B, T>> otherRes = this.otherType.decode(ops, input);
      return original.flatMap(o -> otherRes.map(r -> Pair.of(this.applyFunc.apply((A)o.getFirst(), (B)r.getFirst()), o.getSecond())));
   }

   public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
      return this.codec.encode(input, ops, prefix);
   }

   @Override
   public String toString() {
      return "UnionCodec[" + this.codec + ", " + this.otherType + "]";
   }
}
