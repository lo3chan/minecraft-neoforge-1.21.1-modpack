package net.mehvahdjukaar.moonlight.api.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.DataResult.Error;
import java.util.function.BiPredicate;

public class BestAlternativeCodec<A, B extends A, C extends A> implements Codec<A> {
   private final Codec<B> first;
   private final Codec<C> second;
   private final BiPredicate<B, C> chooseFirst;

   public BestAlternativeCodec(Codec<B> first, Codec<C> second, BiPredicate<B, C> chooseFirst) {
      this.first = first;
      this.second = second;
      this.chooseFirst = chooseFirst;
   }

   public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
      DataResult<Pair<B, T>> firstRead = this.first.decode(ops, input);
      DataResult<Pair<C, T>> secondRead = this.second.decode(ops, input);
      if (firstRead.isSuccess() && secondRead.isSuccess()) {
         B b = (B)((Pair)firstRead.result().orElseThrow()).getFirst();
         C c = (C)((Pair)secondRead.result().orElseThrow()).getFirst();
         return this.chooseFirst.test(b, c)
            ? firstRead.map(p -> Pair.of(p.getFirst(), p.getSecond()))
            : secondRead.map(p -> Pair.of(p.getFirst(), p.getSecond()));
      } else if (firstRead.isSuccess()) {
         return firstRead.map(p -> Pair.of(p.getFirst(), p.getSecond()));
      } else {
         return secondRead.isSuccess()
            ? secondRead.map(p -> Pair.of(p.getFirst(), p.getSecond()))
            : DataResult.error(
               () -> "Failed to parse either. First: "
                  + ((Error)firstRead.error().orElseThrow()).message()
                  + "; Second: "
                  + ((Error)secondRead.error().orElseThrow()).message()
            );
      }
   }

   public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
      try {
         return this.first.encode(input, ops, prefix);
      } catch (Exception var6) {
         return this.second.encode(input, ops, prefix);
      }
   }

   @Override
   public String toString() {
      return "BestAlternativeCodec[" + this.first + ", " + this.second + "]";
   }
}
