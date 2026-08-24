package net.mehvahdjukaar.moonlight.core.databuddy;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.NullObject;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.DataResult.Error;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigMetadata;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.mehvahdjukaar.moonlight.api.platform.configs.platform.TrackedConfigValue;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigHelper {
   static final Logger LOGGER = LogManager.getLogger();

   private ConfigHelper() {
   }

   public static <T> T register(String modid, Type configType, Function<Builder, T> configFactory) {
      return register(modid, configType, configFactory, null);
   }

   public static <T> T register(String modid, Type configType, Function<Builder, T> configFactory, @Nullable String configName) {
      ModContainer mod = (ModContainer)ModList.get().getModContainerById(modid).get();
      Pair<T, ModConfigSpec> entry = new Builder().configure(configFactory);
      T config = (T)entry.getLeft();
      ModConfigSpec spec = (ModConfigSpec)entry.getRight();
      if (configName == null) {
         mod.registerConfig(configType, spec);
      } else {
         mod.registerConfig(configType, spec, configName + ".toml");
      }

      return config;
   }

   public static <T> ConfigHelper.ConfigObject<T> defineObject(Builder builder, String name, Codec<T> codec, Supplier<T> defaultSupplier) {
      return defineObject(builder, name, codec, defaultSupplier, ConfigMetadata.NONE);
   }

   public static <T> ConfigHelper.ConfigObject<T> defineObject(Builder builder, String name, Codec<T> codec, Supplier<T> defaultSupplier, ConfigMetadata meta) {
      Supplier<Object> lazyDefaultValue = Suppliers.memoize(() -> {
         T defaultValue = (T)defaultSupplier.get();
         DataResult<Object> encodeResult = codec.encodeStart(ConfigHelper.TomlConfigOps.INSTANCE, defaultValue);
         return encodeResult.getOrThrow(s -> new IllegalArgumentException(String.format("Unable to encode default value %s: %s", defaultValue, s)));
      });
      ConfigValue<Object> value = builder.define(name, lazyDefaultValue, Objects::nonNull);
      return new ConfigHelper.ConfigObject<>(value, codec, defaultSupplier, meta);
   }

   public static class ConfigObject<T> implements TrackedConfigValue<T> {
      private final ConfigValue<Object> value;
      private final Codec<T> codec;
      private Object cachedObject;
      private T parsedObject;
      private final java.util.function.Supplier<T> defaultObject;
      private boolean initialized;
      private final ConfigMetadata meta;

      private ConfigObject(ConfigValue<Object> value, Codec<T> codec, Supplier<T> defaultSupplier, ConfigMetadata meta) {
         this.value = value;
         this.codec = codec;
         this.defaultObject = Suppliers.memoize(defaultSupplier);
         this.meta = meta;
      }

      @Nonnull
      @Override
      public T get() {
         this.pollChanged();
         return this.parsedObject;
      }

      public void set(T value) {
         this.setValue(value);
      }

      @Override
      public boolean setValue(T value) {
         boolean[] changed = new boolean[]{false};
         this.codec
            .encodeStart(ConfigHelper.TomlConfigOps.INSTANCE, value)
            .resultOrPartial(e -> ConfigHelper.LOGGER.error("Config failure: Could not save value {} due to encoding error: {}", value, e))
            .ifPresent(serializedObject -> {
               changed[0] = !Objects.equals(this.cachedObject, serializedObject);
               this.value.set(serializedObject);
               this.value.save();
               this.parsedObject = value;
               this.cachedObject = serializedObject;
               this.initialized = true;
            });
         return changed[0];
      }

      private T getReparsedObject(Object obj) {
         DataResult<T> parseResult = this.codec.parse(ConfigHelper.TomlConfigOps.INSTANCE, obj);
         return (T)parseResult.mapOrElse(result -> result, failure -> {
            ConfigHelper.LOGGER.error("Config failure: Using default config value due to parsing error: {}", failure.message());
            return this.defaultObject.get();
         });
      }

      @Override
      public boolean pollChanged() {
         Object freshObject = this.value.get();
         if (!this.initialized) {
            this.cachedObject = freshObject;
            this.parsedObject = this.getReparsedObject(freshObject);
            this.initialized = true;
            return false;
         } else if (!Objects.equals(this.cachedObject, freshObject)) {
            this.cachedObject = freshObject;
            this.parsedObject = this.getReparsedObject(freshObject);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean affectsDynamicPacks() {
         return this.meta.affectsDynamicPacks();
      }

      @Override
      public ConfigReloadType reloadType() {
         return this.meta.reloadType();
      }
   }

   public static class TomlConfigOps implements DynamicOps<Object> {
      public static final ConfigHelper.TomlConfigOps INSTANCE = new ConfigHelper.TomlConfigOps();

      public Object empty() {
         return NullObject.NULL_OBJECT;
      }

      public <U> U convertTo(DynamicOps<U> outOps, Object input) {
         if (input instanceof Config) {
            return (U)this.convertMap(outOps, input);
         } else if (input instanceof Collection) {
            return (U)this.convertList(outOps, input);
         } else if (input == null || input instanceof NullObject) {
            return (U)outOps.empty();
         } else if (input instanceof Enum) {
            return (U)outOps.createString(((Enum)input).name());
         } else if (input instanceof Temporal) {
            return (U)outOps.createString(input.toString());
         } else if (input instanceof String s) {
            return (U)outOps.createString(s);
         } else if (input instanceof Boolean b) {
            return (U)outOps.createBoolean(b);
         } else if (input instanceof Number n) {
            return (U)outOps.createNumeric(n);
         } else {
            throw new UnsupportedOperationException("TomlConfigOps was unable to convert toml value: " + input);
         }
      }

      public DataResult<Number> getNumberValue(Object input) {
         return input instanceof Number n ? DataResult.success(n) : DataResult.error(() -> "Not a number: " + input);
      }

      public DataResult<Boolean> getBooleanValue(Object input) {
         if (input instanceof Boolean b) {
            return DataResult.success(b);
         } else {
            return input instanceof Number n ? DataResult.success(n.intValue() > 0) : DataResult.error(() -> "Not a boolean: " + input);
         }
      }

      public Object createBoolean(boolean value) {
         return value;
      }

      public boolean compressMaps() {
         return false;
      }

      public Object createNumeric(Number i) {
         return i;
      }

      public DataResult<String> getStringValue(Object input) {
         return !(input instanceof Config) && !(input instanceof Collection)
            ? DataResult.success(String.valueOf(input))
            : DataResult.error(() -> "Not a string: " + input);
      }

      public Object createString(String value) {
         return value;
      }

      public DataResult<Object> mergeToList(Object list, List<Object> values) {
         return super.mergeToList(list, values).map(obj -> obj == this.empty() ? new ArrayList() : obj);
      }

      public DataResult<Object> mergeToList(Object list, Object value) {
         if (!(list instanceof Collection) && list != this.empty()) {
            return DataResult.error(() -> "mergeToList called with not a list: " + list, list);
         } else {
            Collection<Object> result = new ArrayList<>();
            if (list != this.empty()) {
               Collection<Object> listAsCollection = (Collection<Object>)list;
               result.addAll(listAsCollection);
            }

            result.add(value);
            return DataResult.success(result);
         }
      }

      public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
         if (!(map instanceof Config) && map != this.empty()) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
         } else {
            DataResult<String> stringResult = this.getStringValue(key);
            Optional<Error<String>> badResult = stringResult.error();
            return badResult.isPresent() ? DataResult.error(() -> "key is not a string: " + key, map) : stringResult.flatMap(s -> {
               Config output = TomlFormat.newConfig();
               if (map != this.empty()) {
                  Config oldConfig = (Config)map;
                  output.addAll(oldConfig);
               }

               output.add(s, value);
               return DataResult.success(output);
            });
         }
      }

      public DataResult<Stream<com.mojang.datafixers.util.Pair<Object, Object>>> getMapValues(Object input) {
         return input instanceof Config config
            ? DataResult.success(config.entrySet().stream().map(entry -> com.mojang.datafixers.util.Pair.of(entry.getKey(), entry.getValue())))
            : DataResult.error(() -> "Not a Config: " + input);
      }

      public Object createMap(Stream<com.mojang.datafixers.util.Pair<Object, Object>> map) {
         Config result = TomlFormat.newConfig();
         map.forEach(p -> result.add((String)this.getStringValue(p.getFirst()).getOrThrow(), p.getSecond()));
         return result;
      }

      public DataResult<Stream<Object>> getStream(Object input) {
         return input instanceof Collection<Object> collection ? DataResult.success(collection.stream()) : DataResult.error(() -> "Not a collection: " + input);
      }

      public Object createList(Stream<Object> input) {
         return input.toList();
      }

      public Object remove(Object input, String key) {
         if (input instanceof Config oldConfig) {
            Config result = TomlFormat.newConfig();
            oldConfig.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.add(entry.getKey(), entry.getValue()));
            return result;
         } else {
            return input;
         }
      }

      @Override
      public String toString() {
         return "TOML";
      }
   }
}
