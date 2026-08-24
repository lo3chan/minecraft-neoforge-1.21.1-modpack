package net.mehvahdjukaar.moonlight.api.platform.configs.platform;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.mehvahdjukaar.codecui.SchemaCodec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigMetadata;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.databuddy.ConfigHelper;
import net.mehvahdjukaar.moonlight.platform.ConfigHacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.http.annotation.Experimental;
import org.jetbrains.annotations.Nullable;

public class ConfigBuilderImpl extends ConfigBuilder {
   private final List<TrackedConfigValue<?>> trackedValues = new ArrayList<>();
   private final Builder builder;
   private final Deque<String> categoryStack = new ArrayDeque<>();

   public static ConfigBuilder create(ResourceLocation name, ConfigType type) {
      return new ConfigBuilderImpl(name, type);
   }

   public ConfigBuilderImpl(ResourceLocation name, ConfigType type) {
      super(name, type);
      this.builder = new Builder();
      ConfigHacks.init();
   }

   private void addUiRow(String name, ConfigOption<?> value) {
      this.recordOption(value);
      this.noteDefined(name, value, null);
   }

   private Component uiTitle(String name) {
      return this.description(name);
   }

   @Nullable
   private Component uiDescription(String name) {
      String key = this.tooltipKey(name);
      return this.translations.containsKey(key) ? this.tooltip(name) : null;
   }

   private ConfigOption.UnsupportedValue unsupported(String name, Supplier<?> handle) {
      return new ConfigOption.UnsupportedValue(this.uiTitle(name), this.uiDescription(name), (Supplier<Object>)handle);
   }

   private ConfigMetadata pendingMeta() {
      return new ConfigMetadata(this.pendingReload, this.pendingDynamicPacks);
   }

   private <T> T track(T value) {
      if (value instanceof TrackedConfigValue<?> trackedValue) {
         this.trackedValues.add(trackedValue);
      }

      return value;
   }

   @Override
   public String currentCategory() {
      return this.categoryStack.peekFirst();
   }

   @Nullable
   @Override
   public String parentCategory() {
      if (this.categoryStack.size() < 2) {
         return null;
      } else {
         Iterator<String> it = this.categoryStack.descendingIterator();
         it.next();
         return it.next();
      }
   }

   protected ForgeConfigHolder buildHolder() {
      return new ForgeConfigHolder(this.getName(), this.builder.build(), this.type, this.buildChangeCallback(), this.trackedValues, this.getUiRoot());
   }

   public ConfigBuilderImpl push(String category) {
      this.builder.push(category);
      this.categoryStack.push(category);
      this.noteCategoryName(category);
      this.uiPush(Component.translatable(this.translationKey("")));
      return this;
   }

   public ConfigBuilderImpl pop() {
      this.flushPendingComment();
      this.builder.pop();
      this.categoryStack.pop();
      this.uiPop();
      return this;
   }

   @Override
   public Supplier<Boolean> define(String name, boolean defaultValue) {
      this.addTranslationsAndComments(name);
      BooleanValue value = this.builder.define(name, defaultValue);
      ForgeConfigValue<Boolean, Boolean> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.BooleanValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue));
      return w;
   }

   @Override
   public Supplier<Integer> define(String name, int defaultValue, int min, int max) {
      return this.defineInt(name, defaultValue, min, max, false);
   }

   @Override
   public Supplier<Integer> defineSlider(String name, int defaultValue, int min, int max) {
      return this.defineInt(name, defaultValue, min, max, true);
   }

   private Supplier<Integer> defineInt(String name, int defaultValue, int min, int max, boolean slider) {
      this.addTranslationsAndComments(name);
      IntValue value = this.builder.defineInRange(name, defaultValue, min, max);
      ForgeConfigValue<Integer, Integer> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(
         name,
         (ConfigOption<?>)(slider
            ? new ConfigOption.IntSliderValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, min, max)
            : new ConfigOption.IntValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, min, max))
      );
      return w;
   }

   @Override
   public Supplier<Double> define(String name, double defaultValue, double min, double max) {
      return this.defineDouble(name, defaultValue, min, max, false);
   }

   @Override
   public Supplier<Double> defineSlider(String name, double defaultValue, double min, double max) {
      return this.defineDouble(name, defaultValue, min, max, true);
   }

   private Supplier<Double> defineDouble(String name, double defaultValue, double min, double max, boolean slider) {
      this.addTranslationsAndComments(name);
      DoubleValue value = this.builder.defineInRange(name, defaultValue, min, max);
      ForgeConfigValue<Double, Double> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(
         name,
         (ConfigOption<?>)(slider
            ? new ConfigOption.DoubleSliderValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, min, max)
            : new ConfigOption.DoubleValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, min, max))
      );
      return w;
   }

   @Override
   public Supplier<Double> definePercentage(String name, double defaultValue) {
      this.addTranslationsAndComments(name);
      DoubleValue value = this.builder.defineInRange(name, defaultValue, 0.0, 1.0);
      ForgeConfigValue<Double, Double> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.PercentValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue));
      return w;
   }

   @Experimental
   @Override
   public Supplier<Float> define(String name, float defaultValue, float min, float max) {
      return this.defineFloat(name, defaultValue, min, max, false);
   }

   @Override
   public Supplier<Float> defineSlider(String name, float defaultValue, float min, float max) {
      return this.defineFloat(name, defaultValue, min, max, true);
   }

   private Supplier<Float> defineFloat(String name, float defaultValue, float min, float max, boolean slider) {
      this.addTranslationsAndComments(name);
      DoubleValue value = this.builder.defineInRange(name, defaultValue, min, max);
      var w = (<unrepresentable>)this.track(new ForgeConfigValue<Float, Double>(value, this.pendingMeta()) {
         Float map(Double value) {
            return value.floatValue();
         }

         Double unmap(Float value) {
            return (double)value.floatValue();
         }
      });
      this.addUiRow(
         name,
         (ConfigOption<?>)(slider
            ? new ConfigOption.FloatSliderValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, min, max)
            : new ConfigOption.FloatValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, min, max))
      );
      return w;
   }

   @Override
   public Supplier<Integer> defineColor(String name, int defaultValue, boolean hasAlpha) {
      this.addTranslationsAndComments(name);
      Codec<Integer> codec = ColorUtils.codec(hasAlpha);
      String def = (String)codec.encodeStart(JavaOps.INSTANCE, defaultValue).getOrThrow();
      ConfigValue<String> value = this.builder.define(name, def, o -> o instanceof String s && ColorUtils.isValidString(s));
      ForgeConfigValue<Integer, String> w = this.track(ForgeConfigValue.fromString(value, codec, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.ColorValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, hasAlpha));
      return w;
   }

   @Override
   public Supplier<String> define(String name, String defaultValue, Predicate<Object> validator) {
      this.addTranslationsAndComments(name);
      ConfigValue<String> value = this.builder.define(name, defaultValue, validator);
      ForgeConfigValue<String, String> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.StringValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, validator));
      return w;
   }

   @Override
   protected Supplier<String> defineRegexInternal(String name, String defaultValue) {
      this.addTranslationsAndComments(name);
      ConfigValue<String> value = this.builder.define(name, defaultValue, ConfigBuilder.REGEX_CHECK);
      ForgeConfigValue<String, String> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.RegexValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue));
      return w;
   }

   @Override
   protected Supplier<String> defineChoiceInternal(
      String name, String defaultValue, Predicate<Object> validator, Supplier<List<String>> options, Function<String, ItemStack> icon
   ) {
      this.addTranslationsAndComments(name);
      ConfigValue<String> value = this.builder.define(name, defaultValue, validator);
      ForgeConfigValue<String, String> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.DropdownValue(this.uiTitle(name), this.uiDescription(name), w, defaultValue, options, icon));
      return w;
   }

   public <T> Supplier<T> define(String name, Supplier<T> defaultValue, Predicate<Object> validator) {
      this.addTranslationsAndComments(name);
      ConfigValue<T> value = this.builder.define(name, defaultValue, validator);
      ForgeConfigValue<T, T> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(name, this.unsupported(name, w));
      return w;
   }

   @Override
   public <T extends String> Supplier<List<String>> define(String name, List<? extends T> defaultValue, Predicate<Object> predicate) {
      this.addTranslationsAndComments(name);
      ConfigValue<? extends List<? extends T>> value = this.builder.defineList(name, defaultValue, predicate);
      ForgeConfigValue<List<String>, List<String>> w = this.track(ForgeConfigValue.simple((ConfigValue<List<String>>)value, this.pendingMeta()));
      this.addUiRow(name, new ConfigOption.ListValue(this.uiTitle(name), this.uiDescription(name), w, List.copyOf(defaultValue), s -> predicate.test(s)));
      return w;
   }

   @Override
   protected Supplier<List<String>> defineListInternal(
      String name, List<String> defaultValue, Predicate<Object> entryValidator, Supplier<List<String>> options, Function<String, ItemStack> icon
   ) {
      this.addTranslationsAndComments(name);
      ConfigValue<List<? extends String>> value = this.builder.defineList(name, defaultValue, entryValidator);
      ForgeConfigValue<List<String>, List<String>> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(
         name, new ConfigOption.ListValue(this.uiTitle(name), this.uiDescription(name), w, List.copyOf(defaultValue), entryValidator::test, options, icon)
      );
      return w;
   }

   @Override
   public <T> Supplier<T> defineObject(String name, com.google.common.base.Supplier<T> defaultSupplier, Codec<T> rawCodec) {
      SchemaCodec<T> codec = SchemaCodec.wrap(rawCodec);
      this.addTranslationsAndComments(name);
      if (!this.writeObjectsAsJson) {
         ConfigHelper.ConfigObject<T> w = this.track(ConfigHelper.defineObject(this.builder, name, codec, defaultSupplier, this.pendingMeta()));
         this.addUiRow(name, new ConfigOption.SchemaValue<>(this.uiTitle(name), this.uiDescription(name), w, defaultSupplier::get, codec));
         return w;
      } else {
         com.google.common.base.Supplier<JsonElement> jsonSupplier = () -> {
            DataResult<JsonElement> e = codec.encodeStart(JsonOps.INSTANCE, defaultSupplier.get());
            Optional<JsonElement> json = e.resultOrPartial(s -> {
               throw new RuntimeException("Invalid default value for config " + name + ": " + s);
            });
            if (json.isEmpty()) {
               throw new RuntimeException("Invalid default value for config " + name);
            } else {
               return json.get();
            }
         };
         ForgeConfigValue<T, String> w = this.track(
            ForgeConfigValue.codec(
               this.builder
                  .define(
                     name,
                     () -> ((JsonElement)jsonSupplier.get()).toString().replace(" ", "").replace("\"", "'"),
                     o -> o != null && ((JsonElement)jsonSupplier.get()).getClass().isAssignableFrom(o.getClass())
                  ),
               codec,
               this.pendingMeta()
            )
         );
         this.addUiRow(name, new ConfigOption.SchemaValue<>(this.uiTitle(name), this.uiDescription(name), w, defaultSupplier::get, codec));
         return w;
      }
   }

   @Override
   public <T> Supplier<List<T>> defineObjectList(String name, com.google.common.base.Supplier<List<T>> defaultSupplier, Codec<T> codec) {
      this.builder.comment("This is a list. Add more entries with syntax [[...]]");
      return super.defineObjectList(name, defaultSupplier, codec);
   }

   @Override
   public Supplier<JsonElement> defineJson(String path, JsonElement defaultValue) {
      this.addTranslationsAndComments(path);
      ForgeConfigValue<JsonElement, String> w = this.track(
         ForgeConfigValue.json(this.builder.define(path, defaultValue.toString().replace(" ", "").replace("\"", "'")), this.pendingMeta())
      );
      this.addUiRow(path, new ConfigOption.JsonValue(this.uiTitle(path), this.uiDescription(path), w));
      return w;
   }

   @Override
   public Supplier<JsonElement> defineJson(String path, Supplier<JsonElement> defaultValue) {
      this.addTranslationsAndComments(path);
      com.google.common.base.Supplier<JsonElement> lazyDefaultValue = Suppliers.memoize(defaultValue::get);
      ForgeConfigValue<JsonElement, String> w = this.track(
         ForgeConfigValue.json(
            this.builder
               .define(
                  path,
                  () -> ((JsonElement)lazyDefaultValue.get()).toString().replace(" ", "").replace("\"", "'"),
                  o -> o != null && ((JsonElement)lazyDefaultValue.get()).getClass().isAssignableFrom(o.getClass())
               ),
            this.pendingMeta()
         )
      );
      this.addUiRow(path, new ConfigOption.JsonValue(this.uiTitle(path), this.uiDescription(path), w));
      return w;
   }

   @Override
   public <V extends Enum<V>> Supplier<V> define(String name, V defaultValue) {
      this.addTranslationsAndComments(name);
      EnumValue<V> value = this.builder.defineEnum(name, defaultValue);
      ForgeConfigValue<V, V> w = this.track(ForgeConfigValue.simple(value, this.pendingMeta()));
      this.addUiRow(
         name, new ConfigOption.EnumValue<>(this.uiTitle(name), this.uiDescription(name), w, defaultValue, defaultValue.getDeclaringClass().getEnumConstants())
      );
      return w;
   }

   @Override
   protected void forwardReloadFlag(ConfigReloadType type) {
      if (type == ConfigReloadType.GAME_RESTART) {
         this.builder.gameRestart();
      } else if (type == ConfigReloadType.WORLD_RELOAD && !CompatHandler.CONFIGURED) {
         this.builder.worldRestart();
      }
   }

   @Override
   protected void addTranslationsAndComments(String name) {
      this.builder.translation(this.translationKey(name));
      this.forwardPendingComment();
      super.addTranslationsAndComments(name);
   }

   private void forwardPendingComment() {
      String toForward = this.pollCommentToForward();
      if (toForward != null) {
         this.builder.comment(toForward);
      }
   }
}
