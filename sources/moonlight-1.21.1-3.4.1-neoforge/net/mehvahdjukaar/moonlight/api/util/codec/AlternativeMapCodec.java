package net.mehvahdjukaar.moonlight.api.util.codec;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DataResult.Error;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class AlternativeMapCodec<A> extends MapCodec<A> {
   private final MapCodec<A> first;
   private final MapCodec<A> second;
   private final A defaultValue;

   public AlternativeMapCodec(MapCodec<A> first, MapCodec<A> second, A defaultValue) {
      this.first = first;
      this.second = second;
      this.defaultValue = defaultValue;
   }

   public <T> Stream<T> keys(DynamicOps<T> ops) {
      return Streams.concat(new Stream[]{this.first.keys(ops), this.second.keys(ops)});
   }

   public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
      DataResult<A> firstRead = this.first.decode(ops, input);
      if (firstRead.isSuccess()) {
         return firstRead;
      } else {
         DataResult<A> secondRead = this.second.decode(ops, input);
         if (secondRead.isSuccess()) {
            return secondRead;
         } else if (firstRead.hasResultOrPartial()) {
            return firstRead;
         } else if (secondRead.hasResultOrPartial()) {
            return secondRead;
         } else {
            return this.defaultValue != null
               ? DataResult.success(this.defaultValue)
               : DataResult.error(
                  () -> "Failed to parse either. First: "
                     + ((Error)firstRead.error().orElseThrow()).message()
                     + "; Second: "
                     + ((Error)secondRead.error().orElseThrow()).message()
               );
         }
      }
   }

   public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
      return this.first.encode(input, ops, prefix);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         AlternativeMapCodec that = (AlternativeMapCodec)obj;
         return Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.first, this.second);
   }

   public String toString() {
      return "AlternativeMapCodec[first=" + this.first + ", second=" + this.second + "]";
   }

   public static <B> MapCodec<Optional<B>> optionalAlias(Codec<B> codec, String primaryName, String alias) {
      return new AlternativeMapCodec<>(
         codec.fieldOf(primaryName).xmap(Optional::of, Optional::get), codec.fieldOf(alias).xmap(Optional::of, Optional::get), Optional.empty()
      );
   }

   public static <B> MapCodec<B> alias(Codec<B> codec, String primaryName, String alias) {
      return new AlternativeMapCodec(codec.fieldOf(primaryName), codec.fieldOf(alias), null);
   }
}
