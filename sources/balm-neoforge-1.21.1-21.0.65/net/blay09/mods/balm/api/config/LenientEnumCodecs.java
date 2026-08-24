package net.blay09.mods.balm.api.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public class LenientEnumCodecs {
   public static <T extends Enum<T>> Codec<T> fromValues(Supplier<T[]> valuesSupplier) {
      T[] values = (T[])valuesSupplier.get();
      Function<String, T> nameLookup = createNameLookup(values, Function.identity());
      ToIntFunction<T> indexLookup = Util.createIndexLookup(Arrays.asList(values));
      return new LenientEnumCodecs.LenientEnumCodec<>(values, nameLookup, indexLookup);
   }

   private static <T extends Enum<T>> Function<String, T> createNameLookup(T[] values, Function<String, String> keyFunction) {
      if (values.length > 16) {
         Map<String, T> map = Arrays.<Enum>stream(values)
            .collect(Collectors.toMap(value -> keyFunction.apply(getSerializedName((Enum<?>)value).toLowerCase(Locale.ROOT)), Function.identity()));
         return name -> name == null ? null : map.get(name.toLowerCase(Locale.ROOT));
      } else {
         return name -> {
            for (T value : values) {
               if (keyFunction.apply(getSerializedName(value)).equalsIgnoreCase(name)) {
                  return value;
               }
            }

            return null;
         };
      }
   }

   private static String getSerializedName(Enum<?> enumValue) {
      return enumValue instanceof StringRepresentable stringRepresentable ? stringRepresentable.getSerializedName() : enumValue.name().toLowerCase(Locale.ROOT);
   }

   static class LenientEnumCodec<S extends Enum<S>> implements Codec<S> {
      private final Codec<S> codec;

      public LenientEnumCodec(S[] values, Function<String, S> nameLookup, ToIntFunction<S> indexLookup) {
         this.codec = ExtraCodecs.orCompressed(
            Codec.stringResolver(LenientEnumCodecs::getSerializedName, nameLookup),
            ExtraCodecs.idResolverCodec(indexLookup, p_304986_ -> p_304986_ >= 0 && p_304986_ < values.length ? values[p_304986_] : null, -1)
         );
      }

      public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T value) {
         return this.codec.decode(ops, value);
      }

      public <T> DataResult<T> encode(S input, DynamicOps<T> ops, T prefix) {
         return this.codec.encode(input, ops, prefix);
      }
   }
}
