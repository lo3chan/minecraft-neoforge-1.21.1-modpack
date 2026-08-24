package me.lucko.spark.lib.adventure.text.event;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.util.Services;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public final class DataComponentValueConverterRegistry {
   private static final Set<DataComponentValueConverterRegistry.Provider> PROVIDERS = Services.services(DataComponentValueConverterRegistry.Provider.class);

   private DataComponentValueConverterRegistry() {
   }

   public static Set<Key> knownProviders() {
      return Collections.unmodifiableSet(PROVIDERS.stream().map(DataComponentValueConverterRegistry.Provider::id).collect(Collectors.toSet()));
   }

   @NotNull
   public static <O extends DataComponentValue> O convert(@NotNull final Class<O> target, @NotNull final Key key, @NotNull final DataComponentValue in) {
      if (target.isInstance(in)) {
         return target.cast(in);
      } else {
         DataComponentValueConverterRegistry.RegisteredConversion converter = DataComponentValueConverterRegistry.ConversionCache.converter(
            (Class<? extends DataComponentValue>)in.getClass(), target
         );
         if (converter == null) {
            throw new IllegalArgumentException(
               "There is no data holder converter registered to convert from a " + in.getClass() + " instance to a " + target + " (on field " + key + ")"
            );
         } else {
            try {
               return (O)((DataComponentValueConverterRegistry.Conversion<DataComponentValue, ?>)converter.conversion).convert(key, in);
            } catch (Exception var5) {
               throw new IllegalStateException(
                  "Failed to convert data component value of type "
                     + in.getClass()
                     + " to type "
                     + target
                     + " due to an error in a converter provided by "
                     + converter.provider.asString()
                     + "!",
                  var5
               );
            }
         }
      }
   }

   @NonExtendable
   public interface Conversion<I, O> extends Examinable {
      @NotNull
      static <I1, O1> DataComponentValueConverterRegistry.Conversion<I1, O1> convert(
         @NotNull final Class<I1> src, @NotNull final Class<O1> dst, @NotNull final BiFunction<Key, I1, O1> op
      ) {
         return new DataComponentValueConversionImpl(
            Objects.requireNonNull((Class<I>)src, "src"), Objects.requireNonNull((Class<O>)dst, "dst"), Objects.requireNonNull((BiFunction<Key, I, O>)op, "op")
         );
      }

      @Contract(
         pure = true
      )
      @NotNull
      Class<I> source();

      @Contract(
         pure = true
      )
      @NotNull
      Class<O> destination();

      @NotNull
      O convert(@NotNull final Key key, @NotNull final I input);
   }

   static final class ConversionCache {
      private static final ConcurrentMap<Class<?>, ConcurrentMap<Class<?>, DataComponentValueConverterRegistry.RegisteredConversion>> CACHE = new ConcurrentHashMap<>();
      private static final Map<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> CONVERSIONS = collectConversions();

      private static Map<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> collectConversions() {
         Map<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> collected = new ConcurrentHashMap<>();

         for (DataComponentValueConverterRegistry.Provider provider : DataComponentValueConverterRegistry.PROVIDERS) {
            Key id = Objects.requireNonNull(provider.id(), () -> "ID of provider " + provider + " is null");

            for (DataComponentValueConverterRegistry.Conversion<?, ?> conv : provider.conversions()) {
               collected.computeIfAbsent(conv.source(), $ -> ConcurrentHashMap.newKeySet())
                  .add(new DataComponentValueConverterRegistry.RegisteredConversion(id, conv));
            }
         }

         for (Entry<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> entry : collected.entrySet()) {
            entry.setValue(Collections.unmodifiableSet(entry.getValue()));
         }

         return new ConcurrentHashMap<>(collected);
      }

      static DataComponentValueConverterRegistry.RegisteredConversion compute(final Class<?> src, final Class<?> dst) {
         Deque<Class<?>> sourceTypes = new ArrayDeque<>();
         sourceTypes.add(src);

         Class<?> sourcePtr;
         while ((sourcePtr = sourceTypes.poll()) != null) {
            Set<DataComponentValueConverterRegistry.RegisteredConversion> conversions = CONVERSIONS.get(sourcePtr);
            if (conversions != null) {
               DataComponentValueConverterRegistry.RegisteredConversion nearest = null;

               for (DataComponentValueConverterRegistry.RegisteredConversion potential : conversions) {
                  Class<?> potentialDst = potential.conversion.destination();
                  if (dst.equals(potentialDst)) {
                     return potential;
                  }

                  if (dst.isAssignableFrom(potentialDst) && (nearest == null || potentialDst.isAssignableFrom(nearest.conversion.destination()))) {
                     nearest = potential;
                  }
               }

               if (nearest != null) {
                  return nearest;
               }
            }

            addSupertypes(sourcePtr, sourceTypes);
         }

         return DataComponentValueConverterRegistry.RegisteredConversion.NONE;
      }

      private static void addSupertypes(final Class<?> clazz, final Deque<Class<?>> queue) {
         if (clazz.getSuperclass() != null) {
            queue.add(clazz.getSuperclass());
         }

         queue.addAll(Arrays.asList(clazz.getInterfaces()));
      }

      @Nullable
      static DataComponentValueConverterRegistry.RegisteredConversion converter(
         final Class<? extends DataComponentValue> src, final Class<? extends DataComponentValue> dst
      ) {
         DataComponentValueConverterRegistry.RegisteredConversion result = CACHE.computeIfAbsent(src, $ -> new ConcurrentHashMap<>())
            .computeIfAbsent(dst, $$ -> compute(src, dst));
         return result == DataComponentValueConverterRegistry.RegisteredConversion.NONE ? null : result;
      }
   }

   public interface Provider {
      @NotNull
      Key id();

      @NotNull
      Iterable<DataComponentValueConverterRegistry.Conversion<?, ?>> conversions();
   }

   static final class RegisteredConversion {
      static final DataComponentValueConverterRegistry.RegisteredConversion NONE = new DataComponentValueConverterRegistry.RegisteredConversion(null, null);
      final Key provider;
      final DataComponentValueConverterRegistry.Conversion<?, ?> conversion;

      RegisteredConversion(final Key provider, final DataComponentValueConverterRegistry.Conversion<?, ?> conversion) {
         this.provider = provider;
         this.conversion = conversion;
      }
   }
}
