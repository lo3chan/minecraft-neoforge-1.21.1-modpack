package com.anthonyhilyard.iceberg.neoforge.config;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.config.IIcebergConfigSpec;
import com.anthonyhilyard.iceberg.services.IIcebergConfigSpecBuilder;
import com.anthonyhilyard.iceberg.util.UnsafeUtil;
import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionListener;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.neoforged.fml.Logging;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class NeoForgeIcebergConfigSpec implements IConfigSpec, IIcebergConfigSpec {
   private final Map<List<String>, String> levelComments;
   private final Map<List<String>, String> levelTranslationKeys;
   private final UnmodifiableConfig spec;
   private final UnmodifiableConfig values;
   @Nullable
   private ILoadedConfig loadedConfig;
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Joiner LINE_JOINER = Joiner.on("\n");
   private static final Joiner DOT_JOINER = Joiner.on(".");
   private static final Splitter DOT_SPLITTER = Splitter.on(".");

   private NeoForgeIcebergConfigSpec(
      UnmodifiableConfig spec, UnmodifiableConfig values, Map<List<String>, String> levelComments, Map<List<String>, String> levelTranslationKeys
   ) {
      this.spec = Config.copy(spec);
      this.values = Config.copy(values);
      this.levelComments = Map.copyOf(levelComments);
      this.levelTranslationKeys = Map.copyOf(levelTranslationKeys);

      try {
         Field exceptionHandlerField = FileWatcher.class.getDeclaredField("exceptionHandler");
         UnsafeUtil.setField(
            exceptionHandlerField,
            FileWatcher.defaultInstance(),
            (Consumer<Exception>)e -> LogManager.getLogger().warn(Logging.CORE, "An error occurred while reloading config:", e)
         );
      } catch (Exception var6) {
      }
   }

   public boolean isEmpty() {
      return this.spec.isEmpty();
   }

   public String getLevelComment(List<String> path) {
      return this.levelComments.get(path);
   }

   public String getLevelTranslationKey(List<String> path) {
      return this.levelTranslationKeys.get(path);
   }

   public void acceptConfig(@Nullable ILoadedConfig config) {
      this.loadedConfig = config;
      if (config != null && !this.isCorrect(config.config())) {
         String configName = config.config() instanceof FileConfig fileConfig ? fileConfig.getNioPath().toString() : config.toString();
         Iceberg.LOGGER.warn("Configuration file {} is not correct. Correcting ", configName);
         this.correct(
            config.config(),
            (action, path, incorrectValue, correctedValue) -> Iceberg.LOGGER
               .warn(
                  "Incorrect key {} was corrected from {} to its default, {}. {}",
                  DOT_JOINER.join(path),
                  incorrectValue,
                  correctedValue,
                  incorrectValue == correctedValue ? "This seems to be an error." : ""
               ),
            (action, path, incorrectValue, correctedValue) -> Iceberg.LOGGER
               .debug("The comment on key {} does not match the spec. This may create a backup.", DOT_JOINER.join(path))
         );
         config.save();
      }

      this.afterReload();
   }

   public void validateSpec(ModConfig config) {
   }

   @Override
   public boolean isLoaded() {
      return this.loadedConfig != null;
   }

   public UnmodifiableConfig getSpec() {
      return this.spec;
   }

   public UnmodifiableConfig getValues() {
      return this.values;
   }

   private void forEachValue(Iterable<Object> configValues, Consumer<ConfigValue<?>> consumer) {
      configValues.forEach(value -> {
         if (value instanceof ConfigValue<?> configValue) {
            consumer.accept(configValue);
         } else if (value instanceof Config innerConfig) {
            this.forEachValue(innerConfig.valueMap().values(), consumer);
         }
      });
   }

   public void afterReload() {
      this.resetCaches(RestartType.NONE);
   }

   @Internal
   public void resetCaches(RestartType restartType) {
      this.forEachValue(this.getValues().valueMap().values(), configValue -> {
         if (configValue.getSpec() == null || configValue.getSpec().restartType() == restartType) {
            configValue.clearCache();
         }
      });
   }

   public void save() {
      Preconditions.checkNotNull(this.loadedConfig, "Cannot save config value without assigned Config object present");
      this.loadedConfig.save();
   }

   public boolean isCorrect(UnmodifiableCommentedConfig config) {
      LinkedList<String> parentPath = Lists.newLinkedList();
      return this.correct(this.spec, config, parentPath, Collections.unmodifiableList(parentPath), (a, b, c, d) -> {}, null, true) == 0;
   }

   public void correct(CommentedConfig config) {
      this.correct(config, (action, path, incorrectValue, correctedValue) -> {}, null);
   }

   public int correct(CommentedConfig config, CorrectionListener listener) {
      return this.correct(config, listener, null);
   }

   public int correct(CommentedConfig config, CorrectionListener listener, CorrectionListener commentListener) {
      LinkedList<String> parentPath = Lists.newLinkedList();
      return this.correct(this.spec, config, parentPath, Collections.unmodifiableList(parentPath), listener, commentListener, false);
   }

   private int correct(
      UnmodifiableConfig spec,
      UnmodifiableCommentedConfig config,
      LinkedList<String> parentPath,
      List<String> parentPathUnmodifiable,
      CorrectionListener listener,
      @Nullable CorrectionListener commentListener,
      boolean dryRun
   ) {
      int count = 0;
      Map<String, Object> specMap = spec.valueMap();
      Map<String, Object> configMap = config.valueMap();

      for (Entry<String, Object> specEntry : specMap.entrySet()) {
         String key = specEntry.getKey();
         Object specValue = specEntry.getValue();
         Object configValue = configMap.get(key);
         CorrectionAction action = configValue == null ? CorrectionAction.ADD : CorrectionAction.REPLACE;
         parentPath.addLast(key);
         String subConfigComment = null;
         if (specValue instanceof ValueSpec valueSpec && valueSpec.getDefault() instanceof UnmodifiableConfig) {
            subConfigComment = valueSpec.getComment();
            specValue = valueSpec.getDefault();
         }

         if (specValue instanceof Config specConfig) {
            if (configValue instanceof Config) {
               count += this.correct(
                  specConfig,
                  configValue instanceof CommentedConfig commentedConfig ? commentedConfig : CommentedConfig.copy((Config)configValue),
                  parentPath,
                  parentPathUnmodifiable,
                  listener,
                  commentListener,
                  dryRun
               );
               if (count > 0 && dryRun) {
                  return count;
               }
            } else {
               if (dryRun) {
                  return 1;
               }

               CommentedConfig newValue = ((CommentedConfig)config).createSubConfig();
               configMap.put(key, newValue);
               listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
               count++;
               if (specConfig instanceof NeoForgeIcebergConfigSpec.MutableSubconfig) {
                  specConfig.valueMap().forEach((k, v) -> newValue.valueMap().put(k, v instanceof ValueSpec vSpec ? vSpec.getDefault() : v));
               } else {
                  count += this.correct((UnmodifiableConfig)specValue, newValue, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
               }
            }

            String newComment = subConfigComment == null ? this.levelComments.get(parentPath) : subConfigComment;
            String oldComment = config.getComment(key);
            if (!this.stringsMatchNormalizingNewLines(oldComment, newComment)) {
               if (commentListener != null) {
                  commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, newComment);
               }

               if (dryRun) {
                  return 1;
               }

               ((CommentedConfig)config).setComment(key, newComment);
            }
         } else if (specValue instanceof ValueSpec valueSpec) {
            if (!valueSpec.test(configValue)) {
               if (dryRun) {
                  return 1;
               }

               Object newValue = valueSpec.correct(configValue);
               configMap.put(key, newValue);
               listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
               count++;
            }

            String oldComment = config.getComment(key);
            if (!this.stringsMatchNormalizingNewLines(oldComment, valueSpec.getComment())) {
               if (commentListener != null) {
                  commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, valueSpec.getComment());
               }

               if (dryRun) {
                  return 1;
               }

               ((CommentedConfig)config).setComment(key, valueSpec.getComment());
            }
         } else if (spec instanceof NeoForgeIcebergConfigSpec.MutableSubconfig subconfig && configMap.containsKey(key)) {
            if (!subconfig.keyValidator().test(key)) {
               if (dryRun) {
                  return 1;
               }

               listener.onCorrect(CorrectionAction.REMOVE, parentPathUnmodifiable, key, null);
               configMap.remove(key);
               count++;
            }

            if (!subconfig.valueValidator().test(configMap.get(key))) {
               if (dryRun) {
                  return 1;
               }

               listener.onCorrect(CorrectionAction.REMOVE, parentPathUnmodifiable, configMap.get(key), null);
               configMap.remove(key);
               count++;
            }
         }

         parentPath.removeLast();
      }

      Iterator<Entry<String, Object>> iterator = configMap.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<String, Object> entry = iterator.next();
         if (!(spec instanceof NeoForgeIcebergConfigSpec.MutableSubconfig) && !specMap.containsKey(entry.getKey())) {
            if (dryRun) {
               return 1;
            }

            iterator.remove();
            parentPath.addLast(entry.getKey());
            listener.onCorrect(CorrectionAction.REMOVE, parentPathUnmodifiable, entry.getValue(), null);
            parentPath.removeLast();
            count++;
         }
      }

      return count;
   }

   private boolean stringsMatchNormalizingNewLines(@Nullable String string1, @Nullable String string2) {
      boolean blank1 = string1 == null || string1.isBlank();
      boolean blank2 = string2 == null || string2.isBlank();
      if (blank1 != blank2) {
         return false;
      } else {
         return blank1 && blank2 ? true : string1.replaceAll("\r\n", "\n").equals(string2.replaceAll("\r\n", "\n"));
      }
   }

   public static ValueSpec createValueSpec(
      String comment, String langKey, boolean worldRestart, Class<?> clazz, Supplier<?> defaultSupplier, Predicate<Object> validator, RestartType restartType
   ) {
      Objects.requireNonNull(defaultSupplier, "Default supplier can not be null!");
      Objects.requireNonNull(validator, "Validator can not be null!");
      ValueSpec result = UnsafeUtil.newInstance(ValueSpec.class);

      try {
         Field commentField = ValueSpec.class.getDeclaredField("comment");
         Field langKeyField = ValueSpec.class.getDeclaredField("langKey");
         Field rangeField = ValueSpec.class.getDeclaredField("range");
         Field worldRestartField = ValueSpec.class.getDeclaredField("worldRestart");
         Field clazzField = ValueSpec.class.getDeclaredField("clazz");
         Field supplierField = ValueSpec.class.getDeclaredField("supplier");
         Field validatorField = ValueSpec.class.getDeclaredField("validator");
         Field restartTypeField = ValueSpec.class.getDeclaredField("restartType");
         UnsafeUtil.setField(commentField, result, comment);
         UnsafeUtil.setField(langKeyField, result, langKey);
         UnsafeUtil.setField(rangeField, result, null);
         UnsafeUtil.setField(worldRestartField, result, worldRestart);
         UnsafeUtil.setField(clazzField, result, clazz);
         UnsafeUtil.setField(supplierField, result, defaultSupplier);
         UnsafeUtil.setField(validatorField, result, validator);
         UnsafeUtil.setField(restartTypeField, result, restartType);
      } catch (Exception var16) {
         Iceberg.LOGGER.warn("Failed to instantiate ValueSpec!");
         Iceberg.LOGGER.warn(ExceptionUtils.getStackTrace(var16));
      }

      return result;
   }

   public ILoadedConfig loadedConfig() {
      return this.loadedConfig;
   }

   private static List<String> split(String path) {
      return Lists.newArrayList(DOT_SPLITTER.split(path));
   }

   public static class Builder extends net.neoforged.neoforge.common.ModConfigSpec.Builder implements IIcebergConfigSpecBuilder {
      public NeoForgeIcebergConfigSpec.Builder comment(String comment) {
         return (NeoForgeIcebergConfigSpec.Builder)super.comment(comment);
      }

      public NeoForgeIcebergConfigSpec.Builder comment(String... comment) {
         return (NeoForgeIcebergConfigSpec.Builder)super.comment(comment);
      }

      public NeoForgeIcebergConfigSpec.Builder translation(String translationKey) {
         return (NeoForgeIcebergConfigSpec.Builder)super.translation(translationKey);
      }

      public NeoForgeIcebergConfigSpec.Builder worldRestart() {
         return (NeoForgeIcebergConfigSpec.Builder)super.worldRestart();
      }

      public NeoForgeIcebergConfigSpec.Builder push(String path) {
         return (NeoForgeIcebergConfigSpec.Builder)super.push(path);
      }

      public NeoForgeIcebergConfigSpec.Builder push(List<String> path) {
         return (NeoForgeIcebergConfigSpec.Builder)super.push(path);
      }

      public NeoForgeIcebergConfigSpec.Builder pop() {
         return (NeoForgeIcebergConfigSpec.Builder)super.pop();
      }

      public NeoForgeIcebergConfigSpec.Builder pop(int count) {
         return (NeoForgeIcebergConfigSpec.Builder)super.pop(count);
      }

      private NeoForgeIcebergConfigSpec finishBuild() {
         NeoForgeIcebergConfigSpec result = null;

         try {
            Field valuesField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("values");
            Field storageField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("spec");
            Field levelCommentsField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("levelComments");
            Field levelTranslationKeysField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("levelTranslationKeys");
            List<ConfigValue<?>> values = UnsafeUtil.getField(valuesField, this);
            Config storage = UnsafeUtil.getField(storageField, this);
            Map<List<String>, String> levelComments = UnsafeUtil.getField(levelCommentsField, this);
            Map<List<String>, String> levelTranslationKeys = UnsafeUtil.getField(levelTranslationKeysField, this);
            Config valueCfg = Config.of(Config.getDefaultMapCreator(true, true), InMemoryFormat.withSupport(ConfigValue.class::isAssignableFrom));
            values.forEach(v -> valueCfg.set(v.getPath(), v));
            NeoForgeIcebergConfigSpec ret = new NeoForgeIcebergConfigSpec(
               storage.unmodifiable(), valueCfg.unmodifiable(), Collections.unmodifiableMap(levelComments), Collections.unmodifiableMap(levelTranslationKeys)
            );
            Field specField = ConfigValue.class.getDeclaredField("spec");
            values.forEach(v -> UnsafeUtil.setField(specField, v, ret));
            result = ret;
         } catch (Exception var13) {
            Iceberg.LOGGER.warn("Failed to build NeoForgeIcebergConfigSpec!");
            Iceberg.LOGGER.warn(ExceptionUtils.getStackTrace(var13));
         }

         return result;
      }

      @Override
      public void reset() {
         try {
            Field valuesField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("values");
            Field storageField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("spec");
            Field levelCommentsField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("levelComments");
            Field levelTranslationKeysField = net.neoforged.neoforge.common.ModConfigSpec.Builder.class.getDeclaredField("levelTranslationKeys");
            List<ConfigValue<?>> values = UnsafeUtil.getField(valuesField, this);
            Config storage = UnsafeUtil.getField(storageField, this);
            Map<List<String>, String> levelComments = UnsafeUtil.getField(levelCommentsField, this);
            Map<List<String>, String> levelTranslationKeys = UnsafeUtil.getField(levelTranslationKeysField, this);
            storage.clear();
            levelComments.clear();
            levelTranslationKeys.clear();
            values.clear();
         } catch (Exception var9) {
            Iceberg.LOGGER.warn(ExceptionUtils.getStackTrace(var9));
         }
      }

      @Override
      public <T> Pair<T, IIcebergConfigSpec> finish(Function<IIcebergConfigSpecBuilder, T> consumer) {
         T o = consumer.apply(this);
         return Pair.of(o, this.finishBuild());
      }

      private <T, S extends ConfigValue<T>> ConfigValueWrapper<T, S> wrap(S value) {
         return new ConfigValueWrapper<>(value);
      }

      @Override
      public <T> Supplier<T> add(String path, T defaultValue) {
         ConfigValue<T> value = this.define(path, defaultValue);
         return () -> this.<T, ConfigValue<T>>wrap(value).get();
      }

      @Override
      public <T> Supplier<T> add(String path, T defaultValue, Predicate<Object> validator) {
         ConfigValue<T> value = this.define(path, defaultValue, validator);
         return () -> this.<T, ConfigValue<T>>wrap(value).get();
      }

      @Override
      public <V extends Comparable<? super V>> Supplier<V> addInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
         ConfigValue<V> value = this.defineInRange(path, defaultValue, min, max, clazz);
         return () -> this.<V, ConfigValue<V>>wrap(value).get();
      }

      @Override
      public <T> Supplier<T> addInList(String path, T defaultValue, Collection<? extends T> acceptableValues) {
         ConfigValue<T> value = this.defineInList(path, defaultValue, acceptableValues);
         return () -> this.<T, ConfigValue<T>>wrap(value).get();
      }

      @Override
      public <T> Supplier<List<? extends T>> addList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
         ConfigValue<List<? extends T>> value = this.defineList(path, defaultValue, elementValidator);
         return () -> this.<List<? extends T>, ConfigValue<List<? extends T>>>wrap(value).get();
      }

      @Override
      public <T> Supplier<List<? extends T>> addListAllowEmpty(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
         ConfigValue<List<? extends T>> value = this.defineListAllowEmpty(path, defaultValue, elementValidator);
         return () -> this.<List<? extends T>, ConfigValue<List<? extends T>>>wrap(value).get();
      }

      @Override
      public <V extends Enum<V>> Supplier<V> addEnum(String path, V defaultValue) {
         EnumValue<V> value = this.defineEnum(path, defaultValue);
         return () -> (V)this.wrap(value).get();
      }

      @Override
      public <V extends Enum<V>> Supplier<V> addEnum(String path, V defaultValue, Predicate<Object> validator) {
         EnumValue<V> value = this.defineEnum(path, defaultValue, validator);
         return () -> (V)this.wrap(value).get();
      }

      @Override
      public Supplier<Boolean> add(String path, boolean defaultValue) {
         BooleanValue value = this.define(path, defaultValue);
         return () -> (Boolean)this.wrap(value).get();
      }

      @Override
      public Supplier<Double> addInRange(String path, double defaultValue, double min, double max) {
         DoubleValue value = this.defineInRange(path, defaultValue, min, max);
         return () -> (Double)this.wrap(value).get();
      }

      @Override
      public Supplier<Integer> addInRange(String path, int defaultValue, int min, int max) {
         IntValue value = this.defineInRange(path, defaultValue, min, max);
         return () -> (Integer)this.wrap(value).get();
      }

      @Override
      public Supplier<Long> addInRange(String path, long defaultValue, long min, long max) {
         LongValue value = this.defineInRange(path, defaultValue, min, max);
         return () -> (Long)this.wrap(value).get();
      }

      @Override
      public Supplier<Map<String, Object>> addSubconfig(
         String path, Map<String, Object> defaultValue, Predicate<Object> keyValidator, Predicate<Object> valueValidator
      ) {
         return this.addSubconfig(NeoForgeIcebergConfigSpec.split(path), defaultValue, keyValidator, valueValidator);
      }

      public Supplier<Map<String, Object>> addSubconfig(
         List<String> path, Map<String, Object> defaultValue, Predicate<Object> keyValidator, Predicate<Object> valueValidator
      ) {
         return this.addSubconfig(path, () -> defaultValue, keyValidator, valueValidator);
      }

      public Supplier<Map<String, Object>> addSubconfig(
         String path, Supplier<Map<String, Object>> defaultSupplier, Predicate<Object> keyValidator, Predicate<Object> valueValidator
      ) {
         return this.addSubconfig(NeoForgeIcebergConfigSpec.split(path), defaultSupplier, keyValidator, valueValidator);
      }

      public Supplier<Map<String, Object>> addSubconfig(
         List<String> path, Supplier<Map<String, Object>> defaultSupplier, Predicate<Object> keyValidator, Predicate<Object> valueValidator
      ) {
         UnmodifiableConfig defaultConfig = Config.of(defaultSupplier, TomlFormat.instance());
         ConfigValue<Config> value = this.define(
            path, () -> NeoForgeIcebergConfigSpec.MutableSubconfig.copy(defaultConfig, keyValidator, valueValidator), o -> o != null
         );
         return () -> this.<Config, ConfigValue<Config>>wrap(value).get().valueMap();
      }
   }

   public static final class MutableSubconfig extends AbstractCommentedConfig {
      private final ConfigFormat<?> configFormat;
      private final Predicate<Object> keyValidator;
      private final Predicate<Object> valueValidator;
      private static ValueSpec defaultValueSpec = null;

      MutableSubconfig(
         UnmodifiableConfig toCopy, ConfigFormat<?> configFormat, boolean concurrent, Predicate<Object> keyValidator, Predicate<Object> valueValidator
      ) {
         super(toCopy, concurrent);
         this.configFormat = configFormat;
         this.keyValidator = keyValidator;
         this.valueValidator = valueValidator;
      }

      public ValueSpec defaultValueSpec() {
         if (defaultValueSpec == null) {
            defaultValueSpec = NeoForgeIcebergConfigSpec.createValueSpec(null, null, false, Object.class, () -> null, this.valueValidator, RestartType.NONE);
         }

         return defaultValueSpec;
      }

      public ConfigFormat<?> configFormat() {
         return this.configFormat;
      }

      public Predicate<Object> keyValidator() {
         return this.keyValidator;
      }

      public Predicate<Object> valueValidator() {
         return this.valueValidator;
      }

      public static NeoForgeIcebergConfigSpec.MutableSubconfig copy(UnmodifiableConfig config, Predicate<Object> keyValidator, Predicate<Object> valueValidator) {
         return new NeoForgeIcebergConfigSpec.MutableSubconfig(config, config.configFormat(), false, keyValidator, valueValidator);
      }

      public CommentedConfig createSubConfig() {
         throw new UnsupportedOperationException("Can't make a subconfig of a mutable subconfig!");
      }

      public AbstractCommentedConfig clone() {
         throw new UnsupportedOperationException("Can't clone a mutable subconfig!");
      }
   }
}
