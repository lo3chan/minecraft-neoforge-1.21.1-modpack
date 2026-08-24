package dev.latvian.mods.kubejs.component;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DataResult.Success;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.DataComponentTypeInfoRegistry;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.EvaluatorException;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public interface DataComponentWrapper {
   DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType(
      object -> Component.translatableEscape("arguments.item.component.unknown", new Object[]{object})
   );
   Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType(
      (object, object2) -> Component.translatableEscape("arguments.item.component.malformed", new Object[]{object, object2})
   );
   SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT = new SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"));
   TypeInfo COMPONENT_TYPE = TypeInfo.of(DataComponentType.class);
   Lazy<Map<DataComponentType<?>, TypeInfo>> TYPE_INFOS = Lazy.identityMap(map -> {
      DataComponentTypeInfoRegistry registry = map::put;
      registry.scanClass(DataComponents.class);
      KubeJSPlugins.forEachPlugin(registry, KubeJSPlugin::registerDataComponentTypeDescriptions);
   });
   Lazy<Set<DataComponentType<?>>> VISUAL_DIFFERENCE = Lazy.of(() -> {
      HashSet<DataComponentType<?>> set = new HashSet<>();
      set.add(DataComponents.DAMAGE);
      set.add(DataComponents.MAX_DAMAGE);
      set.add(DataComponents.ENCHANTMENTS);
      set.add(DataComponents.STORED_ENCHANTMENTS);
      set.add(DataComponents.CUSTOM_MODEL_DATA);
      set.add(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
      set.add(DataComponents.DYED_COLOR);
      set.add(DataComponents.MAP_COLOR);
      set.add(DataComponents.POTION_CONTENTS);
      set.add(DataComponents.TRIM);
      set.add(DataComponents.ENTITY_DATA);
      set.add(DataComponents.BLOCK_ENTITY_DATA);
      set.add(DataComponents.FIREWORK_EXPLOSION);
      set.add(DataComponents.FIREWORKS);
      set.add(DataComponents.PROFILE);
      set.add(DataComponents.BANNER_PATTERNS);
      set.add(DataComponents.BASE_COLOR);
      set.add(DataComponents.POT_DECORATIONS);
      set.add(DataComponents.BLOCK_STATE);
      return set;
   });

   static TypeInfo getTypeInfo(DataComponentType<?> type) {
      return TYPE_INFOS.get().getOrDefault(type, TypeInfo.NONE);
   }

   static DataComponentType<?> wrapType(Object object) {
      return object instanceof DataComponentType ? (DataComponentType)object : (DataComponentType)BuiltInRegistries.DATA_COMPONENT_TYPE.get(ID.mc(object));
   }

   static DataComponentMap readMap(@Nullable DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      if (!reader.canRead()) {
         return DataComponentMap.EMPTY;
      } else {
         Builder builder = null;
         if (reader.canRead() && reader.peek() == '[') {
            reader.skip();

            while (reader.canRead() && reader.peek() != ']') {
               reader.skipWhitespace();
               DataComponentType<?> dataComponentType = readComponentType(reader);
               reader.skipWhitespace();
               reader.expect('=');
               reader.skipWhitespace();
               int i = reader.getCursor();
               DataResult<?> dataResult = dataComponentType.codecOrThrow()
                  .parse((DynamicOps)(registryOps == null ? NbtOps.INSTANCE : registryOps), new TagParser(reader).readValue());
               if (builder == null) {
                  builder = DataComponentMap.builder();
               }

               builder.set(dataComponentType, Cast.to(dataResult.getOrThrow(string -> {
                  reader.setCursor(i);
                  return ERROR_MALFORMED_COMPONENT.createWithContext(reader, dataComponentType.toString(), string);
               })));
               reader.skipWhitespace();
               if (!reader.canRead() || reader.peek() != ',') {
                  break;
               }

               reader.skip();
               reader.skipWhitespace();
               if (!reader.canRead()) {
                  throw ERROR_EXPECTED_COMPONENT.createWithContext(reader);
               }
            }

            reader.expect(']');
         }

         if (reader.canRead() && reader.peek() == '{') {
            CompoundTag tag = new TagParser(reader).readStruct();
            if (builder == null) {
               builder = DataComponentMap.builder();
            }

            builder.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         }

         return builder == null ? DataComponentMap.EMPTY : builder.build();
      }
   }

   static DataComponentPatch readPatch(@Nullable DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      if (!reader.canRead()) {
         return DataComponentPatch.EMPTY;
      } else {
         net.minecraft.core.component.DataComponentPatch.Builder builder = null;
         if (reader.canRead() && reader.peek() == '[') {
            reader.skip();

            while (reader.canRead() && reader.peek() != ']') {
               reader.skipWhitespace();
               boolean remove = reader.canRead() && reader.peek() == '!';
               if (remove) {
                  reader.skip();
               }

               DataComponentType<?> dataComponentType = readComponentType(reader);
               if (remove) {
                  reader.skipWhitespace();
                  if (reader.canRead() && reader.peek() != ']') {
                     reader.expect(',');
                     reader.skipWhitespace();
                  }

                  if (builder == null) {
                     builder = DataComponentPatch.builder();
                  }

                  builder.remove(dataComponentType);
               } else {
                  reader.skipWhitespace();
                  reader.expect('=');
                  reader.skipWhitespace();
                  int i = reader.getCursor();
                  DataResult<?> dataResult = dataComponentType.codecOrThrow()
                     .parse((DynamicOps)(registryOps == null ? NbtOps.INSTANCE : registryOps), new TagParser(reader).readValue());
                  if (builder == null) {
                     builder = DataComponentPatch.builder();
                  }

                  builder.set(dataComponentType, Cast.to(dataResult.getOrThrow(string -> {
                     reader.setCursor(i);
                     return ERROR_MALFORMED_COMPONENT.createWithContext(reader, dataComponentType.toString(), string);
                  })));
                  reader.skipWhitespace();
                  if (!reader.canRead() || reader.peek() != ',') {
                     break;
                  }

                  reader.skip();
                  reader.skipWhitespace();
                  if (!reader.canRead()) {
                     throw ERROR_EXPECTED_COMPONENT.createWithContext(reader);
                  }
               }
            }

            reader.expect(']');
         }

         if (reader.canRead() && reader.peek() == '{') {
            CompoundTag tag = new TagParser(reader).readStruct();
            if (builder == null) {
               builder = DataComponentPatch.builder();
            }

            builder.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         }

         return builder == null ? DataComponentPatch.EMPTY : builder.build();
      }
   }

   static DataComponentType<?> readComponentType(StringReader stringReader) throws CommandSyntaxException {
      if (!stringReader.canRead()) {
         throw ERROR_EXPECTED_COMPONENT.createWithContext(stringReader);
      } else {
         int i = stringReader.getCursor();
         ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
         DataComponentType<?> dataComponentType = (DataComponentType<?>)BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourceLocation);
         if (dataComponentType != null && !dataComponentType.isTransient()) {
            return dataComponentType;
         } else {
            stringReader.setCursor(i);
            throw ERROR_UNKNOWN_COMPONENT.createWithContext(stringReader, resourceLocation);
         }
      }
   }

   static DataComponentPredicate readPredicate(@Nullable DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      DataComponentMap map = reader.canRead() ? readMap(registryOps, reader) : DataComponentMap.EMPTY;
      return map.isEmpty() ? DataComponentPredicate.EMPTY : DataComponentPredicate.allOf(map);
   }

   static boolean filter(Object from, TypeInfo target) {
      return from == null
         || from instanceof DataComponentMap
         || from instanceof DataComponentPatch
         || from instanceof Map
         || from instanceof NativeJavaMap
         || from instanceof String s && (s.isEmpty() || s.charAt(0) == '[');
   }

   @Deprecated(
      forRemoval = true
   )
   static DataComponentMap mapOf(@Nullable DynamicOps<Tag> ops, Object o) {
      try {
         return readMap(ops, new StringReader(o.toString()));
      } catch (CommandSyntaxException var3) {
         throw new RuntimeException("Error parsing DataComponentMap from " + o, var3);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   static DataComponentMap mapOrEmptyOf(@Nullable DynamicOps<Tag> ops, Object o) {
      try {
         return readMap(ops, new StringReader(o.toString()));
      } catch (CommandSyntaxException var3) {
         return DataComponentMap.EMPTY;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   static DataComponentPatch patchOf(@Nullable DynamicOps<Tag> ops, Object o) {
      try {
         return readPatch(ops, new StringReader(o.toString()));
      } catch (CommandSyntaxException var3) {
         throw new RuntimeException("Error parsing DataComponentPatch from " + o, var3);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   static DataComponentPatch patchOrEmptyOf(@Nullable DynamicOps<Tag> ops, Object o) {
      try {
         return readPatch(ops, new StringReader(o.toString()));
      } catch (CommandSyntaxException var3) {
         return DataComponentPatch.EMPTY;
      }
   }

   static DataComponentMap mapOf(Context cx, Object from) {
      return (DataComponentMap)tryMapOf(cx, from)
         .getOrThrow(error -> new KubeRuntimeException("Failed to wrap DataComponentMap: %s".formatted(error)).source(SourceLine.of(cx)));
   }

   static DataComponentPatch patchOf(Context cx, Object from) {
      return (DataComponentPatch)tryPatchOf(cx, from)
         .getOrThrow(error -> new KubeRuntimeException("Failed to wrap DataComponentPatch: %s".formatted(error)).source(SourceLine.of(cx)));
   }

   static DataComponentMap mapOrEmptyOf(Context cx, Object from) {
      return tryMapOf(cx, from).resultOrPartial().orElse(DataComponentMap.EMPTY);
   }

   static DataComponentPatch patchOrEmptyOf(Context cx, Object from) {
      return tryPatchOf(cx, from).resultOrPartial().orElse(DataComponentPatch.EMPTY);
   }

   static DataResult<DataComponentMap> tryMapOf(Context cx, @Nullable Object o) {
      RegistryAccessContainer reg = RegistryAccessContainer.of(cx);

      return switch (o) {
         case null -> {
            DataResult var27 = DataResult.success(DataComponentMap.EMPTY);
            yield var27;
         }
         case DataComponentMap map -> {
            DataResult var26 = DataResult.success(map);
            yield var26;
         }
         case DataComponentPatch patch -> {
            DataResult var25 = DataResult.success(PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, patch));
            yield var25;
         }
         case BaseFunction fn -> {
            DataResult var24 = fnToBuilder(
               cx, DataComponentWrapper.MapBuilder.class, fn, builder -> ((Builder)Util.make(DataComponentMap.builder(), builder)).build()
            );
            yield var24;
         }
         case JsonObject json -> {
            DataResult var23 = DataComponentMap.CODEC.parse(reg.json(), json);
            yield var23;
         }
         case CompoundTag tag -> {
            DataResult var22 = DataComponentMap.CODEC.parse(reg.nbt(), tag);
            yield var22;
         }
         case Map<?, ?> mapx -> {
            Builder builder = DataComponentMap.builder();
            Map<DataComponentType<?>, ?> wrapped = Objects.requireNonNull(cx.optionalMapOf(mapx, COMPONENT_TYPE, TypeInfo.NONE));
            Map<DataComponentType<?>, String> errors = new HashMap<>();

            for (Entry<DataComponentType<?>, ?> entry : wrapped.entrySet()) {
               wrapEntry(cx, entry, null, builder::set, errors::put);
            }

            if (!errors.isEmpty()) {
               StringJoiner joiner = new StringJoiner("; ");
               errors.forEach((type, error) -> {
                  ResourceLocation id = reg.access().registryOrThrow(Registries.DATA_COMPONENT_TYPE).getKeyOrNull(type);
                  joiner.add("'%s' -> %s".formatted(id, error));
               });
               DataResult var20 = DataResult.error(() -> "Invalid component map format, errored input: [%s]".formatted(joiner.toString()), builder.build());
               yield var20;
            } else {
               DataResult var21 = DataResult.success(builder.build());
               yield var21;
            }
         }
         case String s -> {
            DataResult var19;
            try {
               var19 = DataResult.success(readMap(reg.nbt(), new StringReader(s)));
            } catch (CommandSyntaxException var17) {
               var19 = DataResult.error(() -> "Invalid string format '%s' for DataComponentMap: %s".formatted(s, var17.getMessage()));
               yield var19;
            }

            yield var19;
         }
         default -> {
            DataResult var5 = DataResult.error(() -> "Don't know how to convert %s to DataComponentMap!".formatted(o));
            yield var5;
         }
      };
   }

   static DataResult<DataComponentPatch> tryPatchOf(Context cx, @Nullable Object o) {
      RegistryAccessContainer reg = RegistryAccessContainer.of(cx);

      return switch (o) {
         case null -> {
            DataResult var25 = DataResult.success(DataComponentPatch.EMPTY);
            yield var25;
         }
         case DataComponentPatch patch -> {
            DataResult var24 = DataResult.success(patch);
            yield var24;
         }
         case BaseFunction fn -> {
            DataResult var23 = fnToBuilder(
               cx,
               DataComponentWrapper.PatchBuilder.class,
               fn,
               builder -> ((net.minecraft.core.component.DataComponentPatch.Builder)Util.make(DataComponentPatch.builder(), builder)).build()
            );
            yield var23;
         }
         case JsonObject json -> {
            DataResult var22 = DataComponentPatch.CODEC.parse(reg.json(), json);
            yield var22;
         }
         case CompoundTag tag -> {
            DataResult var21 = DataComponentPatch.CODEC.parse(reg.nbt(), tag);
            yield var21;
         }
         case Map<?, ?> map -> {
            net.minecraft.core.component.DataComponentPatch.Builder builder = DataComponentPatch.builder();
            Map<DataComponentType<?>, ?> wrapped = Objects.requireNonNull(cx.optionalMapOf(map, COMPONENT_TYPE, TypeInfo.NONE));
            Map<DataComponentType<?>, String> errors = new HashMap<>();

            for (Entry<DataComponentType<?>, ?> entry : wrapped.entrySet()) {
               wrapEntry(cx, entry, builder::remove, builder::set, errors::put);
            }

            if (!errors.isEmpty()) {
               StringJoiner joiner = new StringJoiner("; ");
               errors.forEach((type, error) -> {
                  ResourceLocation id = reg.access().registryOrThrow(Registries.DATA_COMPONENT_TYPE).getKeyOrNull(type);
                  joiner.add("'%s' -> %s".formatted(id, error));
               });
               DataResult var19 = DataResult.error(() -> "Invalid component map format, errored input: [%s]".formatted(joiner.toString()), builder.build());
               yield var19;
            } else {
               DataResult var20 = DataResult.success(builder.build());
               yield var20;
            }
         }
         case String s -> {
            DataResult var18;
            try {
               var18 = DataResult.success(readPatch(reg.nbt(), new StringReader(s)));
            } catch (CommandSyntaxException var16) {
               var18 = DataResult.error(() -> "Invalid string format '%s' for DataComponentPatch: %s".formatted(s, var16.getMessage()));
               yield var18;
            }

            yield var18;
         }
         default -> {
            DataResult var5 = DataResult.error(() -> "Don't know how to convert %s to DataComponentPatch!".formatted(o));
            yield var5;
         }
      };
   }

   static <T> DataResult<Optional<T>> tryWrapComponent(Context cx, DataComponentType<T> type, Object value) {
      RegistryAccessContainer reg = RegistryAccessContainer.of(cx);
      TypeInfo valueType = getTypeInfo(type);
      if (value != null && !(value instanceof Undefined)) {
         MutableObject<EvaluatorException> evalError = new MutableObject();
         if (valueType.shouldConvert() && cx.canConvert(value, valueType)) {
            try {
               Object converted = cx.jsToJava(value, valueType);
               if (converted != null) {
                  return DataResult.success(Optional.of(Cast.to(converted)));
               }
            } catch (EvaluatorException var11) {
               evalError.setValue(var11);
            }
         }

         Codec<T> codec = type.codec();
         if (codec != null) {
            return (DataResult<Optional<T>>)(switch (codec.parse(reg.json(), JsonUtils.of(cx, value))) {
               case Success<?> success -> success.map(Optional::of);
               case Error<?> error -> error.mapError(
                  err -> evalError.getValue() != null
                     ? "Failed to parse component from type wrappers and codec! Native: %s, Codec: %s"
                        .formatted(((EvaluatorException)evalError.getValue()).details(), err)
                     : "Failed to parse component from codec: %s!".formatted(err)
               );
               default -> throw new MatchException(null, null);
            });
         } else {
            return DataResult.error(() -> "Component has non-serializable type");
         }
      } else {
         return DataResult.success(Optional.empty());
      }
   }

   private static void wrapEntry(
      Context cx,
      Entry<DataComponentType<?>, ?> entry,
      @Nullable Consumer<DataComponentType<?>> ifNull,
      BiConsumer<DataComponentType, Object> builder,
      BiConsumer<DataComponentType<?>, String> errorBuilder
   ) {
      DataComponentType<?> type = entry.getKey();
      Object value = entry.getValue();
      tryWrapComponent(cx, type, value).ifSuccess(success -> {
         if (success.isPresent()) {
            builder.accept(type, success.get());
         } else if (ifNull != null) {
            ifNull.accept(type);
         }
      }).ifError(error -> errorBuilder.accept(type, error.message()));
   }

   private static <B, T> DataResult<T> fnToBuilder(Context cx, Class<B> builderType, BaseFunction fn, Function<B, T> build) {
      try {
         B builder = Cast.to(cx.createInterfaceAdapter(TypeInfo.of(builderType), fn));
         return DataResult.success(build.apply(builder));
      } catch (Exception var5) {
         return DataResult.error(() -> "Failed to create %s from builder: %s".formatted(builderType.toString(), var5));
      }
   }

   static StringBuilder mapToString(StringBuilder builder, @Nullable DynamicOps<Tag> ops, DataComponentMap map) {
      builder.append('[');
      boolean first = true;

      for (TypedDataComponent<?> comp : map) {
         ResourceLocation id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.type());
         Codec<?> codec = comp.type().codec();
         if (id != null && codec != null) {
            if (first) {
               first = false;
            } else {
               builder.append(',');
            }

            builder.append(ID.reduce(id)).append('=');

            try {
               Object value = codec == Codec.BOOL
                  ? comp.value()
                  : codec.encodeStart((DynamicOps)(ops == null ? NbtOps.INSTANCE : ops), Cast.to(comp.value())).getOrThrow();
               builder.append(value);
            } catch (Throwable var9) {
               builder.append("ERROR[").append(var9.getMessage()).append("]");
            }
         }
      }

      builder.append(']');
      return builder;
   }

   static StringBuilder patchToString(StringBuilder builder, @Nullable DynamicOps<Tag> ops, DataComponentPatch patch) {
      builder.append('[');
      boolean first = true;

      for (Entry<DataComponentType<?>, Optional<?>> comp : patch.entrySet()) {
         ResourceLocation id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.getKey());
         Codec<?> codec = comp.getKey().codec();
         if (id != null && codec != null) {
            if (first) {
               first = false;
            } else {
               builder.append(',');
            }

            if (comp.getValue().isPresent()) {
               builder.append(ID.reduce(id)).append('=');

               try {
                  Object value = codec == Codec.BOOL
                     ? comp.getValue().get()
                     : codec.encodeStart((DynamicOps)(ops == null ? NbtOps.INSTANCE : ops), Cast.to(comp.getValue().get())).getOrThrow();
                  builder.append(value);
               } catch (Throwable var9) {
                  builder.append("ERROR[").append(var9.getMessage()).append("]");
               }
            } else {
               builder.append('!').append(ID.reduce(id));
            }
         }
      }

      builder.append(']');
      return builder;
   }

   static DataComponentPatch visualPatch(DataComponentPatch patch) {
      if (patch.isEmpty()) {
         return DataComponentPatch.EMPTY;
      } else {
         net.minecraft.core.component.DataComponentPatch.Builder builder = DataComponentPatch.builder();

         for (Entry<DataComponentType<?>, Optional<?>> entry : patch.entrySet()) {
            if (VISUAL_DIFFERENCE.get().contains(entry.getKey())) {
               if (entry.getValue().isPresent()) {
                  builder.set(entry.getKey(), Cast.to(entry.getValue().get()));
               } else {
                  builder.remove(entry.getKey());
               }
            }
         }

         return builder.build();
      }
   }

   public interface MapBuilder extends Consumer<Builder> {
      void accept(Builder builder);
   }

   public interface PatchBuilder extends Consumer<net.minecraft.core.component.DataComponentPatch.Builder> {
      void accept(net.minecraft.core.component.DataComponentPatch.Builder builder);
   }
}
