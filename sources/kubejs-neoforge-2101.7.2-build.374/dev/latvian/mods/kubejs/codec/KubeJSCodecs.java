package dev.latvian.mods.kubejs.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DataResult.Success;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.type.ClassTypeInfo;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public interface KubeJSCodecs {
   Codec<Character> CHARACTER = Codec.STRING
      .comapFlatMap(
         str -> str.length() != 1 ? DataResult.error(() -> "Expected a single character, got empty string") : DataResult.success(str.charAt(0)),
         Object::toString
      );
   Codec<ResourceLocation> KUBEJS_ID = Codec.STRING.comapFlatMap(s -> {
      try {
         return s.indexOf(58) == -1 ? DataResult.success(KubeJS.id(s)) : DataResult.success(ResourceLocation.parse(s));
      } catch (ResourceLocationException var2) {
         return DataResult.error(() -> "Not a valid resource location: " + s + " " + var2.getMessage());
      }
   }, ID::reduceKjs).stable();
   Codec<Class<?>> ENUM_CLASS = Codec.STRING.comapFlatMap(str -> {
      try {
         Class<?> c = Class.forName(str);
         return !c.isEnum() ? DataResult.error(() -> "Class '" + str + "' is not an enum") : DataResult.success(c);
      } catch (ClassNotFoundException var2) {
         return DataResult.error(() -> "Could not find enum class: " + str);
      }
   }, Class::getName);
   Codec<EnumTypeInfo> ENUM_TYPE_INFO = ENUM_CLASS.comapFlatMap(
      c -> TypeInfo.of(c) instanceof EnumTypeInfo info ? DataResult.success(info) : DataResult.error(() -> "Class " + c.getTypeName() + " is not an enum!"),
      ClassTypeInfo::asClass
   );
   Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
   MapCodec<EntityType<?>> ENTITY_TYPE_FIELD = BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id");
   Codec<Duration> DURATION = Codec.STRING.comapFlatMap(TimeJS::readDuration, Duration::toString);
   Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
   Codec<Map<String, JsonElement>> JSON_MAP = Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON);
   Codec<Integer> NON_NEGATIVE_INT = Codec.INT.validate(v -> v >= 0 ? DataResult.success(v) : DataResult.error(() -> "Value must be non-negative: " + v));
   Codec<Integer> POSITIVE_INT = Codec.INT.validate(v -> v > 0 ? DataResult.success(v) : DataResult.error(() -> "Value must be positive: " + v));
   Codec<Long> NON_NEGATIVE_LONG = Codec.LONG.validate(v -> v >= 0L ? DataResult.success(v) : DataResult.error(() -> "Value must be non-negative: " + v));
   Codec<Long> POSITIVE_LONG = Codec.LONG.validate(v -> v > 0L ? DataResult.success(v) : DataResult.error(() -> "Value must be positive: " + v));
   Codec<Float> NON_NEGATIVE_FLOAT = Codec.FLOAT.validate(v -> v >= 0.0F ? DataResult.success(v) : DataResult.error(() -> "Value must be non-negative: " + v));
   Codec<Float> POSITIVE_FLOAT = Codec.FLOAT.validate(v -> v > 0.0F ? DataResult.success(v) : DataResult.error(() -> "Value must be positive: " + v));
   Codec<Double> NON_NEGATIVE_DOUBLE = Codec.DOUBLE
      .validate(v -> v >= 0.0 ? DataResult.success(v) : DataResult.error(() -> "Value must be non-negative: " + v));
   Codec<Double> POSITIVE_DOUBLE = Codec.DOUBLE.validate(v -> v > 0.0 ? DataResult.success(v) : DataResult.error(() -> "Value must be positive: " + v));

   static <E> Codec<E> stringResolverCodec(Function<E, String> toStringFunction, Function<String, E> fromStringFunction) {
      return Codec.STRING
         .flatXmap(
            str -> Optional.ofNullable(fromStringFunction.apply(str))
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "Unknown element id: " + str)),
            object -> DataResult.success(toStringFunction.apply((E)object))
         );
   }

   static <E extends Enum<E> & StringRepresentable> E byName(Codec<E> codec, String s) {
      return fromJsonOrThrow(new JsonPrimitive(s), codec);
   }

   static <E> E fromJsonOrThrow(JsonElement json, Codec<E> codec) {
      return fromJsonOrThrow(json, codec, str -> {
         throw new JsonSyntaxException("Could not decode element from JSON: " + str);
      });
   }

   static <E> JsonElement toJsonOrThrow(E value, Codec<E> codec) {
      return toJsonOrThrow(value, codec, str -> {
         throw new IllegalArgumentException("Could not encode element to JSON: " + str);
      });
   }

   static <E, X extends Throwable> E fromJsonOrThrow(JsonElement json, Codec<E> codec, Function<String, X> onError) throws X {
      return (E)codec.parse(JsonOps.INSTANCE, json).getOrThrow(onError);
   }

   static <E, X extends Throwable> JsonElement toJsonOrThrow(E value, Codec<E> codec, Function<String, X> onError) throws X {
      return (JsonElement)codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(onError);
   }

   static <T> String getUniqueId(T input, Codec<T> codec) {
      return StringUtilsWrapper.getUniqueId(input, o -> toJsonOrThrow(o, codec));
   }

   static JsonElement numberProviderJson(NumberProvider gen) {
      return toJsonOrThrow(gen, NumberProviders.CODEC);
   }

   static <T> Codec<List<T>> listOfOrSelf(Codec<T> codec) {
      return listOfOrSelf(codec.listOf(), codec);
   }

   static <T> Codec<List<T>> listOfOrSelf(Codec<List<T>> listCodec, Codec<T> codec) {
      return Codec.either(listCodec, codec).xmap(either -> (List)either.map(Function.identity(), List::of), Either::left);
   }

   static <V> Codec<V> or(List<Codec<? extends V>> codecs) {
      return new OrCodec<>((List<Codec<V>>)codecs);
   }

   static <V> Codec<V> or(Codec<? extends V> first, Codec<? extends V> second) {
      return new OrCodec<>(List.of((Codec<V>)first, (Codec<V>)second));
   }

   static Codec<Long> longRangeWithMessage(long min, long max, Function<Long, String> errorMessage) {
      return Codec.LONG.validate(v -> v.compareTo(min) >= 0 && v.compareTo(max) <= 0 ? DataResult.success(v) : DataResult.error(() -> errorMessage.apply(v)));
   }

   static <T> boolean filter(DataResult<T> result, Predicate<T> ifSuccess) {
      return filter(result, ifSuccess, false);
   }

   static <T> boolean filter(DataResult<T> result, Predicate<T> ifSuccess, boolean orElse) {
      return switch (result) {
         case Success var5 -> {
            Success var12 = var5;

            try {
               var13 = var12.value();
            } catch (Throwable var10) {
               throw new MatchException(var10.toString(), var10);
            }

            Object var11 = var13;
            var12 = var5;

            try {
               var12.lifecycle();
            } catch (Throwable var9) {
               throw new MatchException(var9.toString(), var9);
            }

            yield ifSuccess.test((T)var11);
         }
         case Error<T> error -> orElse;
         default -> throw new MatchException(null, null);
      };
   }
}
