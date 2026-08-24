package net.mehvahdjukaar.moonlight.api.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import java.util.Objects;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.core.Moonlight;

public class LenientCodecWithLog<A> extends OptionalFieldCodec<A> {
   private final String name;
   private final Codec<A> elementCodec;

   private LenientCodecWithLog(String name, Codec<A> elementCodec) {
      super(name, elementCodec, true);
      this.name = name;
      this.elementCodec = elementCodec;
   }

   public static <A> MapCodec<A> of(Codec<A> elementCodec, String name, A defaultValue) {
      return of(elementCodec, name).xmap(o -> o.orElse(defaultValue), a -> Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a));
   }

   public static <A> MapCodec<Optional<A>> of(Codec<A> elementCodec, String name) {
      return new LenientCodecWithLog<Optional<A>>(name, elementCodec);
   }

   public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
      T value = (T)input.get(this.name);
      if (value == null) {
         return DataResult.success(Optional.empty());
      } else {
         DataResult<A> parsed = this.elementCodec.parse(ops, value);
         if (parsed.isError()) {
            Moonlight.LOGGER.error("Failed to parse {}: {}. Skipping", this.name, parsed.error());
            return DataResult.success(Optional.empty());
         } else {
            return parsed.map(Optional::of).setPartial(parsed.resultOrPartial());
         }
      }
   }

   public String toString() {
      return "LenientCodecWithLog[" + this.elementCodec + "]";
   }
}
