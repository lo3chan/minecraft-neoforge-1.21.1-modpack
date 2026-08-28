/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.ListBuilder
 *  javax.annotation.Nullable
 */
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
import javax.annotation.Nullable;

public class TupleCodec<F, S>
implements Codec<Pair<F, S>> {
    private final Codec<F> first;
    private final Codec<S> second;

    public static <F, S> TupleCodec<F, S> of(Codec<F> first, Codec<S> second) {
        return new TupleCodec<F, S>(first, second);
    }

    private TupleCodec(Codec<F> first, Codec<S> second) {
        this.first = first;
        this.second = second;
    }

    public <T> DataResult<Pair<Pair<F, S>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            DecoderState decoder = new DecoderState(ops);
            stream.accept(decoder::accept);
            return decoder.build();
        });
    }

    public <T> DataResult<T> encode(Pair<F, S> input, DynamicOps<T> ops, T prefix) {
        ListBuilder builder = ops.listBuilder();
        builder.add(this.first.encodeStart(ops, input.getFirst()));
        builder.add(this.second.encodeStart(ops, input.getSecond()));
        return builder.build(prefix);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TupleCodec) {
            TupleCodec tupleCodec = (TupleCodec)o;
            return Objects.equals(this.first, tupleCodec.first) && Objects.equals(this.second, tupleCodec.second);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public String toString() {
        return "TupleCodec[" + String.valueOf(this.first) + ", " + String.valueOf(this.second) + "]";
    }

    private <R> DataResult<R> createTooShortError(int size) {
        return DataResult.error(() -> "Tuple is too short: " + size + ", expected length is 2");
    }

    private <R> DataResult<R> createTooLongError(int size) {
        return DataResult.error(() -> "Tuple is too long: " + size + ", expected length is 2");
    }

    private class DecoderState<T> {
        private static final DataResult<Unit> INITIAL_RESULT = DataResult.success((Object)Unit.INSTANCE, (Lifecycle)Lifecycle.stable());
        private final DynamicOps<T> ops;
        private final Stream.Builder<T> failed = Stream.builder();
        private DataResult<Unit> result = INITIAL_RESULT;
        @Nullable
        private F firstValue;
        @Nullable
        private S secondValue;
        private int elementCount;

        private DecoderState(DynamicOps<T> ops) {
            this.ops = ops;
        }

        public void accept(T value) {
            ++this.elementCount;
            if (this.firstValue != null && this.secondValue != null) {
                this.failed.add(value);
                return;
            }
            if (this.firstValue == null) {
                DataResult elementResult = TupleCodec.this.first.decode(this.ops, value);
                elementResult.error().ifPresent(error -> this.failed.add(value));
                elementResult.resultOrPartial().ifPresent(pair -> {
                    this.firstValue = pair.getFirst();
                });
                this.result = this.result.apply2stable((result, element) -> result, elementResult);
            } else {
                DataResult elementResult = TupleCodec.this.second.decode(this.ops, value);
                elementResult.error().ifPresent(error -> this.failed.add(value));
                elementResult.resultOrPartial().ifPresent(pair -> {
                    this.secondValue = pair.getFirst();
                });
                this.result = this.result.apply2stable((result, element) -> result, elementResult);
            }
        }

        public DataResult<Pair<Pair<F, S>, T>> build() {
            if (this.elementCount < 2) {
                return TupleCodec.this.createTooShortError(this.elementCount);
            }
            if (this.elementCount > 2) {
                return TupleCodec.this.createTooLongError(this.elementCount);
            }
            Object errors = this.ops.createList(this.failed.build());
            Pair pair = Pair.of((Object)Pair.of(this.firstValue, this.secondValue), (Object)errors);
            return this.result.map(ignored -> pair).setPartial((Object)pair);
        }
    }
}

