package net.mehvahdjukaar.moonlight.api.platform.configs.options;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.mehvahdjukaar.codecui.Schema;
import net.mehvahdjukaar.codecui.SchemaCodec;
import net.mehvahdjukaar.moonlight.api.platform.configs.IConfigValue;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.util.math.Range;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class ConfigOption<T> extends ConfigNode {
   protected final T defaultValue;

   protected ConfigOption(Component title, @Nullable Component description, T defaultValue) {
      super(title, description);
      this.defaultValue = defaultValue;
   }

   public ConfigReloadType reloadType() {
      return this.backingValues().map(IConfigValue::reloadType).max(Comparator.comparingInt(Enum::ordinal)).orElse(ConfigReloadType.NONE);
   }

   protected abstract Stream<IConfigValue<?>> backingValues();

   protected static Stream<IConfigValue<?>> storedValuesOf(Supplier<?>... handles) {
      return Arrays.stream(handles).filter(h -> h instanceof IConfigValue).map(h -> (IConfigValue<?>)h);
   }

   public abstract T get();

   public T defaultValue() {
      return this.defaultValue;
   }

   public abstract void apply(ModConfigHolder var1, Object var2);

   public static class BooleanValue extends ConfigOption.SimpleConfigOption<Boolean> {
      private boolean feature;

      public BooleanValue(Component title, @Nullable Component description, IConfigValue<Boolean> handle, Boolean defaultValue) {
         super(title, description, handle, defaultValue);
      }

      public boolean isFeature() {
         return this.feature;
      }

      @Internal
      public void setFeature(boolean feature) {
         this.feature = feature;
      }
   }

   public static class ColorValue extends ConfigOption.SimpleConfigOption<Integer> {
      public final boolean hasAlpha;

      public ColorValue(Component title, @Nullable Component description, IConfigValue<Integer> handle, Integer defaultValue) {
         this(title, description, handle, defaultValue, true);
      }

      public ColorValue(Component title, @Nullable Component description, IConfigValue<Integer> handle, Integer defaultValue, boolean hasAlpha) {
         super(title, description, handle, defaultValue);
         this.hasAlpha = hasAlpha;
      }
   }

   public static class DoubleSliderValue extends ConfigOption.DoubleValue {
      public DoubleSliderValue(Component title, @Nullable Component description, IConfigValue<Double> handle, Double defaultValue, double min, double max) {
         super(title, description, handle, defaultValue, min, max);
      }
   }

   public static class DoubleValue extends ConfigOption.SimpleConfigOption<Double> {
      public final double min;
      public final double max;

      public DoubleValue(Component title, @Nullable Component description, IConfigValue<Double> handle, Double defaultValue, double min, double max) {
         super(title, description, handle, defaultValue);
         this.min = min;
         this.max = max;
      }
   }

   public static class DropdownValue extends ConfigOption.SimpleConfigOption<String> {
      public final Supplier<List<String>> options;
      @Nullable
      public final Function<String, ItemStack> icon;

      public DropdownValue(
         Component title,
         @Nullable Component description,
         IConfigValue<String> handle,
         String defaultValue,
         Supplier<List<String>> options,
         @Nullable Function<String, ItemStack> icon
      ) {
         super(title, description, handle, defaultValue);
         this.options = options;
         this.icon = icon;
      }
   }

   public static class EnumValue<E extends Enum<E>> extends ConfigOption.SimpleConfigOption<E> {
      public final E[] options;

      public EnumValue(Component title, @Nullable Component description, IConfigValue<E> handle, E defaultValue, E[] options) {
         super(title, description, handle, defaultValue);
         this.options = options;
      }
   }

   public static class FloatSliderValue extends ConfigOption.FloatValue {
      public FloatSliderValue(Component title, @Nullable Component description, IConfigValue<Float> handle, Float defaultValue, float min, float max) {
         super(title, description, handle, defaultValue, min, max);
      }
   }

   public static class FloatValue extends ConfigOption.SimpleConfigOption<Float> {
      public final float min;
      public final float max;

      public FloatValue(Component title, @Nullable Component description, IConfigValue<Float> handle, Float defaultValue, float min, float max) {
         super(title, description, handle, defaultValue);
         this.min = min;
         this.max = max;
      }
   }

   public static class IntSliderValue extends ConfigOption.IntValue {
      public IntSliderValue(Component title, @Nullable Component description, IConfigValue<Integer> handle, Integer defaultValue, int min, int max) {
         super(title, description, handle, defaultValue, min, max);
      }
   }

   public static class IntValue extends ConfigOption.SimpleConfigOption<Integer> {
      public final int min;
      public final int max;

      public IntValue(Component title, @Nullable Component description, IConfigValue<Integer> handle, Integer defaultValue, int min, int max) {
         super(title, description, handle, defaultValue);
         this.min = min;
         this.max = max;
      }
   }

   public static class JsonValue extends ConfigOption<String> {
      public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      private final Supplier<JsonElement> json;

      public JsonValue(Component title, @Nullable Component description, Supplier<JsonElement> json) {
         super(title, description, null);
         this.json = json;
      }

      public String get() {
         return GSON.toJson(this.json.get());
      }

      public String defaultValue() {
         return GSON.toJson(this.json.get());
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
         holder.manuallySetValue(this.json, JsonParser.parseString((String)value));
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return storedValuesOf(new Supplier[]{this.json});
      }
   }

   public static class ListValue extends ConfigOption.SimpleConfigOption<List<String>> {
      @Nullable
      public final Predicate<String> entryValidator;
      @Nullable
      public final Supplier<List<String>> options;
      @Nullable
      public final Function<String, ItemStack> icon;

      public ListValue(
         Component title,
         @Nullable Component description,
         IConfigValue<List<String>> handle,
         List<String> defaultValue,
         @Nullable Predicate<String> entryValidator
      ) {
         this(title, description, handle, defaultValue, entryValidator, null, null);
      }

      public ListValue(
         Component title,
         @Nullable Component description,
         IConfigValue<List<String>> handle,
         List<String> defaultValue,
         @Nullable Predicate<String> entryValidator,
         @Nullable Supplier<List<String>> options,
         @Nullable Function<String, ItemStack> icon
      ) {
         super(title, description, handle, defaultValue);
         this.entryValidator = entryValidator;
         this.options = options;
         this.icon = icon;
      }

      public boolean isValidEntry(String entry) {
         return this.entryValidator == null || this.entryValidator.test(entry);
      }
   }

   public static class PercentValue extends ConfigOption.DoubleValue {
      public PercentValue(Component title, @Nullable Component description, IConfigValue<Double> handle, Double defaultValue) {
         super(title, description, handle, defaultValue, 0.0, 1.0);
      }
   }

   public static class RangeValue extends ConfigOption<Range> {
      public final Supplier<Double> minHandle;
      public final Supplier<Double> maxHandle;
      public final double min;
      public final double max;

      public RangeValue(
         Component title, @Nullable Component description, Supplier<Double> minHandle, Supplier<Double> maxHandle, Range defaultValue, double min, double max
      ) {
         super(title, description, defaultValue);
         this.minHandle = minHandle;
         this.maxHandle = maxHandle;
         this.min = min;
         this.max = max;
      }

      public Range get() {
         return new Range(this.minHandle.get(), this.maxHandle.get());
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
         Range range = (Range)value;
         holder.manuallySetValue(this.minHandle, range.min());
         holder.manuallySetValue(this.maxHandle, range.max());
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return storedValuesOf(new Supplier[]{this.minHandle, this.maxHandle});
      }
   }

   public static class RegexValue extends ConfigOption.StringValue {
      public RegexValue(Component title, @Nullable Component description, IConfigValue<String> handle, String defaultValue) {
         super(title, description, handle, defaultValue, o -> o instanceof String s && isValidRegex(s));
      }

      public static boolean isValidRegex(String s) {
         try {
            Pattern.compile(s);
            return true;
         } catch (Exception var2) {
            return false;
         }
      }
   }

   public static class SchemaValue<T> extends ConfigOption<T> {
      private final IConfigValue<T> handle;
      private final Supplier<T> lazyDefault;
      public final SchemaCodec<T> codec;

      public SchemaValue(Component title, @Nullable Component description, IConfigValue<T> handle, Supplier<T> lazyDefault, SchemaCodec<T> codec) {
         super(title, description, null);
         this.handle = handle;
         this.lazyDefault = lazyDefault;
         this.codec = codec;
      }

      public Schema<T> schema() {
         return this.codec.schema();
      }

      @Override
      public T get() {
         return this.handle.get();
      }

      @Override
      public T defaultValue() {
         return this.lazyDefault.get();
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
         holder.manuallySetValue(this.handle, (T)value);
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return Stream.of(this.handle);
      }
   }

   public abstract static class SimpleConfigOption<T> extends ConfigOption<T> {
      protected final IConfigValue<T> handle;

      protected SimpleConfigOption(Component title, @Nullable Component description, IConfigValue<T> handle, T defaultValue) {
         super(title, description, defaultValue);
         this.handle = handle;
      }

      @Override
      public T get() {
         return this.handle.get();
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
         holder.manuallySetValue(this.handle, (T)value);
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return Stream.of(this.handle);
      }
   }

   public static class StringValue extends ConfigOption.SimpleConfigOption<String> {
      @Nullable
      public final Predicate<Object> validator;

      public StringValue(
         Component title, @Nullable Component description, IConfigValue<String> handle, String defaultValue, @Nullable Predicate<Object> validator
      ) {
         super(title, description, handle, defaultValue);
         this.validator = validator;
      }

      public boolean isValid(String value) {
         return this.validator == null || this.validator.test(value);
      }
   }

   public static class UnsupportedValue extends ConfigOption<Object> {
      private final Supplier<Object> handle;

      public UnsupportedValue(Component title, @Nullable Component description, Supplier<Object> handle) {
         super(title, description, null);
         this.handle = handle;
      }

      @Override
      public Object get() {
         return this.handle.get();
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return storedValuesOf(new Supplier[]{this.handle});
      }
   }

   public static class Vec3Value extends ConfigOption<Vec3> {
      public final Supplier<Double> xHandle;
      public final Supplier<Double> yHandle;
      public final Supplier<Double> zHandle;
      public final double min;
      public final double max;

      public Vec3Value(
         Component title,
         @Nullable Component description,
         Supplier<Double> xHandle,
         Supplier<Double> yHandle,
         Supplier<Double> zHandle,
         Vec3 defaultValue,
         double min,
         double max
      ) {
         super(title, description, defaultValue);
         this.xHandle = xHandle;
         this.yHandle = yHandle;
         this.zHandle = zHandle;
         this.min = min;
         this.max = max;
      }

      public Vec3 get() {
         return new Vec3(this.xHandle.get(), this.yHandle.get(), this.zHandle.get());
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
         Vec3 v = (Vec3)value;
         holder.manuallySetValue(this.xHandle, v.x);
         holder.manuallySetValue(this.yHandle, v.y);
         holder.manuallySetValue(this.zHandle, v.z);
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return storedValuesOf(new Supplier[]{this.xHandle, this.yHandle, this.zHandle});
      }
   }

   public static class Vec3iValue extends ConfigOption<Vec3i> {
      public final Supplier<Integer> xHandle;
      public final Supplier<Integer> yHandle;
      public final Supplier<Integer> zHandle;
      public final int min;
      public final int max;

      public Vec3iValue(
         Component title,
         @Nullable Component description,
         Supplier<Integer> xHandle,
         Supplier<Integer> yHandle,
         Supplier<Integer> zHandle,
         Vec3i defaultValue,
         int min,
         int max
      ) {
         super(title, description, defaultValue);
         this.xHandle = xHandle;
         this.yHandle = yHandle;
         this.zHandle = zHandle;
         this.min = min;
         this.max = max;
      }

      public Vec3i get() {
         return new Vec3i(this.xHandle.get(), this.yHandle.get(), this.zHandle.get());
      }

      @Override
      public void apply(ModConfigHolder holder, Object value) {
         Vec3i v = (Vec3i)value;
         holder.manuallySetValue(this.xHandle, v.getX());
         holder.manuallySetValue(this.yHandle, v.getY());
         holder.manuallySetValue(this.zHandle, v.getZ());
      }

      @Override
      protected Stream<IConfigValue<?>> backingValues() {
         return storedValuesOf(new Supplier[]{this.xHandle, this.yHandle, this.zHandle});
      }
   }
}
