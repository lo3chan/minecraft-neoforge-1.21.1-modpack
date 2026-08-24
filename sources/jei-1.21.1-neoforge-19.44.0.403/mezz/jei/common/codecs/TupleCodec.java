package mezz.jei.common.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import javax.annotation.Nullable;

public class TupleCodec<F, S> implements Codec<Pair<F, S>> {
   private final Codec<F> first;
   private final Codec<S> second;

   public static <F, S> TupleCodec<F, S> of(Codec<F> first, Codec<S> second) {
      return new TupleCodec<>(first, second);
   }

   private TupleCodec(Codec<F> first, Codec<S> second) {
      this.first = first;
      this.second = second;
   }

   public <T> DataResult<Pair<Pair<F, S>, T>> decode(DynamicOps<T> ops, T input) {
      return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
         TupleCodec<F, S>.DecoderState<T> decoder = new TupleCodec.DecoderState<>(ops);
         stream.accept(decoder::accept);
         return decoder.build();
      });
   }

   public <T> DataResult<T> encode(Pair<F, S> input, DynamicOps<T> ops, T prefix) {
      ListBuilder<T> builder = ops.listBuilder();
      builder.add(this.first.encodeStart(ops, input.getFirst()));
      builder.add(this.second.encodeStart(ops, input.getSecond()));
      return builder.build(prefix);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof TupleCodec<?, ?> tupleCodec)
            ? false
            : Objects.equals(this.first, tupleCodec.first) && Objects.equals(this.second, tupleCodec.second);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.first, this.second);
   }

   @Override
   public String toString() {
      return "TupleCodec[" + this.first + ", " + this.second + "]";
   }

   private <R> DataResult<R> createTooShortError(int size) {
      return DataResult.error(() -> "Tuple is too short: " + size + ", expected length is 2");
   }

   private <R> DataResult<R> createTooLongError(int size) {
      return DataResult.error(() -> "Tuple is too long: " + size + ", expected length is 2");
   }

   private class DecoderState<T> {
      private static final DataResult<Unit> INITIAL_RESULT = DataResult.success(Unit.INSTANCE, Lifecycle.stable());
      private final DynamicOps<T> ops;
      private final Builder<T> failed = Stream.builder();
      private DataResult<Unit> result = INITIAL_RESULT;
      @Nullable
      private F firstValue;
      @Nullable
      private S secondValue;
      private int elementCount;

      private DecoderState(final DynamicOps<T> ops) {
         this.ops = ops;
      }

      public void accept(T value) {
         this.elementCount++;
         if (this.firstValue != null && this.secondValue != null) {
            this.failed.add(value);
         } else {
            if (this.firstValue == null) {
               DataResult<Pair<F, T>> elementResult = TupleCodec.this.first.decode(this.ops, value);
               elementResult.error().ifPresent(error -> this.failed.add(value));
               elementResult.resultOrPartial().ifPresent(pair -> this.firstValue = (F)pair.getFirst());
               this.result = this.result.apply2stable((result, element) -> result, elementResult);
            } else {
               DataResult<Pair<S, T>> elementResult = TupleCodec.this.second.decode(this.ops, value);
               elementResult.error().ifPresent(error -> this.failed.add(value));
               elementResult.resultOrPartial().ifPresent(pair -> this.secondValue = (S)pair.getFirst());
               this.result = this.result.apply2stable((result, element) -> result, elementResult);
            }
         }
      }

      public DataResult<Pair<Pair<F, S>, T>> build() {
         if (this.elementCount < 2) {
            return TupleCodec.this.createTooShortError(this.elementCount);
         } else if (this.elementCount > 2) {
            return TupleCodec.this.createTooLongError(this.elementCount);
         } else {
            T errors = (T)this.ops.createList(this.failed.build());
            Pair<Pair<F, S>, T> pair = Pair.of(Pair.of(this.firstValue, this.secondValue), errors);
            return this.result.map(ignored -> pair).setPartial(pair);
         }
      }
   }
}
