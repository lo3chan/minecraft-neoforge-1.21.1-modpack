package codx.codxlib.api.settings;

import codx.codxlib.api.CodxLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CodxSettings {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final List<CodxSettings> REGISTERED = new CopyOnWriteArrayList<>();
   private final String modId;
   private final String fileName;
   private final List<String> legacyFiles;
   private final Runnable onChange;
   private final Logger logger;
   private final Map<String, List<CodxSettings.ConfigValue<?>>> byCategory;
   private final Map<String, CodxSettings.ConfigValue<?>> byName;

   private CodxSettings(CodxSettings.Builder builder) {
      this.modId = builder.modId;
      this.fileName = builder.fileName != null ? builder.fileName : builder.modId + ".json";
      this.legacyFiles = List.copyOf(builder.legacyFiles);
      this.onChange = builder.onChange;
      this.logger = LogManager.getLogger(builder.modId);
      this.byCategory = builder.byCategory;
      Map<String, CodxSettings.ConfigValue<?>> index = new LinkedHashMap<>();

      for (List<CodxSettings.ConfigValue<?>> values : this.byCategory.values()) {
         for (CodxSettings.ConfigValue<?> value : values) {
            CodxSettings.ConfigValue<?> clash = index.putIfAbsent(value.name.toLowerCase(Locale.ROOT), value);
            if (clash != null) {
               this.logger
                  .warn("Duplicate setting name {} in both {} and {} — only the first is reachable by name", value.name, clash.category, value.category);
            }
         }
      }

      this.byName = index;
      REGISTERED.add(this);
   }

   public static CodxSettings.Builder builder(String modId) {
      return new CodxSettings.Builder(modId);
   }

   public static List<CodxSettings> registered() {
      return Collections.unmodifiableList(new ArrayList<>(REGISTERED));
   }

   public String modId() {
      return this.modId;
   }

   public String fileName() {
      return this.fileName;
   }

   public Path configFile() {
      return CodxLib.configDir().resolve(this.fileName);
   }

   public void load() {
      Path file = this.configFile();

      try {
         if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
               JsonElement root = JsonParser.parseReader(reader);
               if (root != null && root.isJsonObject()) {
                  this.read(root.getAsJsonObject());
               }
            }
         } else {
            this.importLegacy();
         }
      } catch (Exception var7) {
         this.logger.warn("Could not read {} — falling back to defaults for anything unparsed", file, var7);
      }

      this.apply();
   }

   public boolean apply() {
      boolean saved = this.save();
      if (this.onChange != null) {
         try {
            this.onChange.run();
         } catch (Exception var3) {
            this.logger.warn("The change hook for {} threw", this.modId, var3);
         }
      }

      return saved;
   }

   public boolean save() {
      Path file = this.configFile();

      try {
         Files.createDirectories(file.getParent());

         try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(this.write(), writer);
         }

         return true;
      } catch (IOException var7) {
         this.logger.warn("Could not write {}", file, var7);
         return false;
      }
   }

   public Collection<CodxSettings.ConfigValue<?>> values() {
      List<CodxSettings.ConfigValue<?>> all = new ArrayList<>();
      this.byCategory.values().forEach(all::addAll);
      return Collections.unmodifiableList(all);
   }

   public Collection<String> categories() {
      return Collections.unmodifiableCollection(this.byCategory.keySet());
   }

   public List<CodxSettings.ConfigValue<?>> category(String name) {
      return Collections.unmodifiableList(this.byCategory.getOrDefault(name, Collections.emptyList()));
   }

   public CodxSettings.ConfigValue<?> find(String name) {
      return name == null ? null : this.byName.get(name.toLowerCase(Locale.ROOT));
   }

   public void resetAll() {
      this.values().forEach(CodxSettings.ConfigValue::reset);
   }

   private void read(JsonObject root) {
      for (Entry<String, List<CodxSettings.ConfigValue<?>>> category : this.byCategory.entrySet()) {
         JsonElement section = root.get(category.getKey());
         if (section != null && section.isJsonObject()) {
            JsonObject obj = section.getAsJsonObject();

            for (CodxSettings.ConfigValue<?> value : category.getValue()) {
               JsonElement element = obj.get(value.name);
               if (element != null) {
                  try {
                     value.read(element);
                  } catch (Exception var10) {
                     this.logger.warn("Ignoring bad config value {}.{}", category.getKey(), value.name, var10);
                  }
               }
            }
         }
      }
   }

   private JsonObject write() {
      JsonObject root = new JsonObject();

      for (Entry<String, List<CodxSettings.ConfigValue<?>>> category : this.byCategory.entrySet()) {
         JsonObject obj = new JsonObject();

         for (CodxSettings.ConfigValue<?> value : category.getValue()) {
            if (value.comment != null) {
               obj.add("// " + value.name, new JsonPrimitive(value.comment));
            }

            obj.add(value.name, value.write());
         }

         root.add(category.getKey(), obj);
      }

      return root;
   }

   private void importLegacy() {
      for (String legacy : this.legacyFiles) {
         Path file = CodxLib.configDir().resolve(legacy);
         if (Files.exists(file)) {
            try {
               int applied = legacy.endsWith(".toml") ? this.readLegacyToml(file) : this.readLegacyJson(file);
               this.logger.info("Imported {} setting(s) from the older {} into {}", applied, legacy, this.fileName);
               return;
            } catch (Exception var5) {
               this.logger.warn("Could not import the older {} — starting from defaults", file, var5);
            }
         }
      }
   }

   private int readLegacyJson(Path file) throws IOException {
      int matched;
      try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
         JsonElement root = JsonParser.parseReader(reader);
         if (root != null && root.isJsonObject()) {
            this.read(root.getAsJsonObject());
            matched = 0;

            for (Entry<String, JsonElement> section : root.getAsJsonObject().entrySet()) {
               if (section.getValue().isJsonObject()) {
                  for (String key : section.getValue().getAsJsonObject().keySet()) {
                     if (!key.startsWith("//") && this.find(key) != null) {
                        matched++;
                     }
                  }
               }
            }

            return matched;
         }

         matched = 0;
      }

      return matched;
   }

   private int readLegacyToml(Path file) throws IOException {
      int applied = 0;
      StringBuilder pending = null;
      String pendingKey = null;

      for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
         String line = raw.trim();
         if (pending != null) {
            pending.append(' ').append(line);
            if (line.contains("]")) {
               applied += this.applyToml(pendingKey, pending.toString());
               pending = null;
               pendingKey = null;
            }
         } else if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("[")) {
            int eq = line.indexOf(61);
            if (eq > 0) {
               String key = line.substring(0, eq).trim();
               String value = line.substring(eq + 1).trim();
               if (value.startsWith("[") && !value.contains("]")) {
                  pending = new StringBuilder(value);
                  pendingKey = key;
               } else {
                  applied += this.applyToml(key, value);
               }
            }
         }
      }

      return applied;
   }

   private int applyToml(String key, String value) {
      CodxSettings.ConfigValue<?> target = this.find(key);
      if (target != null && !value.isEmpty()) {
         try {
            return target.readToml(value) ? 1 : 0;
         } catch (Exception var5) {
            this.logger.warn("Ignoring unreadable legacy value {} = {}", key, value);
            return 0;
         }
      } else {
         return 0;
      }
   }

   static String unquote(String value) {
      String trimmed = value.trim();
      return trimmed.length() < 2 || (trimmed.charAt(0) != '"' || !trimmed.endsWith("\"")) && (trimmed.charAt(0) != '\'' || !trimmed.endsWith("'"))
         ? trimmed
         : trimmed.substring(1, trimmed.length() - 1);
   }

   public static final class BooleanValue extends CodxSettings.ConfigValue<Boolean> {
      BooleanValue(String name, String comment, String category, boolean defaultValue) {
         super(name, comment, category, defaultValue);
      }

      public void toggle() {
         this.value = !this.value;
      }

      @Override
      public String typeName() {
         return "boolean";
      }

      @Override
      public List<String> suggestions() {
         return List.of("true", "false");
      }

      @Override
      public boolean setFromString(String text) {
         if ("true".equalsIgnoreCase(text)) {
            this.value = Boolean.TRUE;
            return true;
         } else if ("false".equalsIgnoreCase(text)) {
            this.value = Boolean.FALSE;
            return true;
         } else {
            return false;
         }
      }

      @Override
      void read(JsonElement element) {
         this.value = element.getAsBoolean();
      }

      @Override
      boolean readToml(String text) {
         return this.setFromString(CodxSettings.unquote(text));
      }

      @Override
      JsonElement write() {
         return new JsonPrimitive(this.value);
      }
   }

   public static final class Builder {
      private final String modId;
      private final Deque<String> stack = new ArrayDeque<>();
      private final Map<String, List<CodxSettings.ConfigValue<?>>> byCategory = new LinkedHashMap<>();
      private final List<String> legacyFiles = new ArrayList<>();
      private String fileName;
      private Runnable onChange;
      private String pendingComment;

      private Builder(String modId) {
         this.modId = modId;
      }

      public CodxSettings.Builder fileName(String name) {
         this.fileName = name;
         return this;
      }

      public CodxSettings.Builder legacyFiles(String... names) {
         Collections.addAll(this.legacyFiles, names);
         return this;
      }

      public CodxSettings.Builder onChange(Runnable hook) {
         this.onChange = hook;
         return this;
      }

      public CodxSettings.Builder push(String category) {
         this.stack.addLast(category);
         return this;
      }

      public CodxSettings.Builder pop() {
         this.stack.pollLast();
         return this;
      }

      public CodxSettings.Builder comment(String comment) {
         this.pendingComment = comment;
         return this;
      }

      public CodxSettings.Builder translation(String key) {
         return this;
      }

      public CodxSettings.BooleanValue define(String name, boolean defaultValue) {
         return this.add(new CodxSettings.BooleanValue(name, this.takeComment(), this.currentCategory(), defaultValue));
      }

      public CodxSettings.StringValue define(String name, String defaultValue) {
         return this.add(new CodxSettings.StringValue(name, this.takeComment(), this.currentCategory(), defaultValue));
      }

      public CodxSettings.IntValue defineInRange(String name, int defaultValue, int min, int max) {
         return this.add(new CodxSettings.IntValue(name, this.takeComment(), this.currentCategory(), defaultValue, min, max));
      }

      public CodxSettings.DoubleValue defineInRange(String name, double defaultValue, double min, double max) {
         return this.add(new CodxSettings.DoubleValue(name, this.takeComment(), this.currentCategory(), defaultValue, min, max));
      }

      public <E extends Enum<E>> CodxSettings.EnumValue<E> defineEnum(String name, E defaultValue) {
         return this.add(new CodxSettings.EnumValue<>(name, this.takeComment(), this.currentCategory(), defaultValue));
      }

      public <T> CodxSettings.ListValue<T> defineList(String name, List<? extends T> defaultValue, Predicate<Object> validator) {
         return this.add(new CodxSettings.ListValue<>(name, this.takeComment(), this.currentCategory(), defaultValue, validator));
      }

      public <T> CodxSettings.Configured<T> configure(Function<CodxSettings.Builder, T> factory) {
         T holder = factory.apply(this);
         return new CodxSettings.Configured<>(holder, new CodxSettings(this));
      }

      public CodxSettings build() {
         return new CodxSettings(this);
      }

      private String currentCategory() {
         return this.stack.isEmpty() ? "general" : String.join(".", this.stack);
      }

      private String takeComment() {
         String comment = this.pendingComment;
         this.pendingComment = null;
         return comment;
      }

      private <V extends CodxSettings.ConfigValue<?>> V add(V value) {
         this.byCategory.computeIfAbsent(this.currentCategory(), k -> new ArrayList<>()).add(value);
         return value;
      }
   }

   public abstract static class ConfigValue<T> {
      final String name;
      final String comment;
      final String category;
      final T defaultValue;
      T value;

      ConfigValue(String name, String comment, String category, T defaultValue) {
         this.name = name;
         this.comment = comment;
         this.category = category;
         this.defaultValue = defaultValue;
         this.value = defaultValue;
      }

      public T get() {
         return this.value;
      }

      public String name() {
         return this.name;
      }

      public String category() {
         return this.category;
      }

      public String comment() {
         return this.comment == null ? "" : this.comment;
      }

      public String asString() {
         return String.valueOf(this.value);
      }

      public String defaultAsString() {
         return String.valueOf(this.defaultValue);
      }

      public boolean isDefault() {
         return this.value.equals(this.defaultValue);
      }

      public void reset() {
         this.value = this.defaultValue;
      }

      public abstract String typeName();

      public String rangeText() {
         return "";
      }

      public List<String> suggestions() {
         return List.of();
      }

      public abstract boolean setFromString(String var1);

      abstract void read(JsonElement var1);

      abstract boolean readToml(String var1);

      abstract JsonElement write();
   }

   public record Configured<T>(T holder, CodxSettings settings) {
   }

   public static final class DoubleValue extends CodxSettings.ConfigValue<Double> {
      private final double min;
      private final double max;

      DoubleValue(String name, String comment, String category, double defaultValue, double min, double max) {
         super(name, comment, category, defaultValue);
         this.min = min;
         this.max = max;
      }

      public double min() {
         return this.min;
      }

      public double max() {
         return this.max;
      }

      public void add(double delta) {
         this.value = this.clamp(this.value + delta);
      }

      private double clamp(double raw) {
         return Math.max(this.min, Math.min(this.max, raw));
      }

      @Override
      public String typeName() {
         return "number";
      }

      @Override
      public String rangeText() {
         return this.min + " to " + this.max;
      }

      @Override
      public List<String> suggestions() {
         return List.of(this.asString(), this.defaultAsString());
      }

      @Override
      public boolean setFromString(String text) {
         try {
            this.value = this.clamp(Double.parseDouble(text.trim()));
            return true;
         } catch (NumberFormatException var3) {
            return false;
         }
      }

      @Override
      void read(JsonElement element) {
         this.value = this.clamp(element.getAsDouble());
      }

      @Override
      boolean readToml(String text) {
         return this.setFromString(CodxSettings.unquote(text));
      }

      @Override
      JsonElement write() {
         return new JsonPrimitive(this.value);
      }
   }

   public static final class EnumValue<E extends Enum<E>> extends CodxSettings.ConfigValue<E> {
      private final List<E> constants;

      EnumValue(String name, String comment, String category, E defaultValue) {
         super(name, comment, category, defaultValue);
         this.constants = List.of(defaultValue.getDeclaringClass().getEnumConstants());
      }

      public List<E> constants() {
         return this.constants;
      }

      public void cycle() {
         this.value = this.constants.get((this.constants.indexOf(this.value) + 1) % this.constants.size());
      }

      @Override
      public String typeName() {
         return "option";
      }

      @Override
      public String rangeText() {
         return String.join(", ", this.suggestions());
      }

      @Override
      public List<String> suggestions() {
         return this.constants.stream().map(Enum::name).toList();
      }

      @Override
      public String asString() {
         return this.value.name();
      }

      @Override
      public String defaultAsString() {
         return this.defaultValue.name();
      }

      @Override
      public boolean setFromString(String text) {
         String wanted = CodxSettings.unquote(text);

         for (E constant : this.constants) {
            if (constant.name().equalsIgnoreCase(wanted)) {
               this.value = constant;
               return true;
            }
         }

         return false;
      }

      @Override
      void read(JsonElement element) {
         this.setFromString(element.getAsString());
      }

      @Override
      boolean readToml(String text) {
         return this.setFromString(text);
      }

      @Override
      JsonElement write() {
         return new JsonPrimitive(this.value.name());
      }
   }

   public static final class IntValue extends CodxSettings.ConfigValue<Integer> {
      private final int min;
      private final int max;

      IntValue(String name, String comment, String category, int defaultValue, int min, int max) {
         super(name, comment, category, defaultValue);
         this.min = min;
         this.max = max;
      }

      public int min() {
         return this.min;
      }

      public int max() {
         return this.max;
      }

      public void add(int delta) {
         this.value = this.clamp((long)this.value.intValue() + delta);
      }

      private int clamp(long raw) {
         return (int)Math.max((long)this.min, Math.min((long)this.max, raw));
      }

      @Override
      public String typeName() {
         return "integer";
      }

      @Override
      public String rangeText() {
         return this.min + " to " + this.max;
      }

      @Override
      public List<String> suggestions() {
         return List.of(String.valueOf(this.value), this.defaultAsString());
      }

      @Override
      public boolean setFromString(String text) {
         try {
            this.value = this.clamp(Long.parseLong(text.trim()));
            return true;
         } catch (NumberFormatException var3) {
            return false;
         }
      }

      @Override
      void read(JsonElement element) {
         this.value = this.clamp(element.getAsLong());
      }

      @Override
      boolean readToml(String text) {
         return this.setFromString(CodxSettings.unquote(text));
      }

      @Override
      JsonElement write() {
         return new JsonPrimitive(this.value);
      }
   }

   public static final class ListValue<T> extends CodxSettings.ConfigValue<List<? extends T>> {
      private final Predicate<Object> validator;

      ListValue(String name, String comment, String category, List<? extends T> defaultValue, Predicate<Object> validator) {
         super(name, comment, category, defaultValue);
         this.validator = validator;
      }

      public List<String> entries() {
         return this.value.stream().map(String::valueOf).toList();
      }

      @Override
      public String typeName() {
         return "list";
      }

      @Override
      public String asString() {
         return String.join(", ", this.entries());
      }

      @Override
      public String defaultAsString() {
         return String.join(", ", this.defaultValue.stream().map(String::valueOf).toList());
      }

      @Override
      public boolean setFromString(String text) {
         List<Object> parsed = new ArrayList<>();

         for (String entry : text.split(",")) {
            String trimmed = CodxSettings.unquote(entry);
            if (!trimmed.isEmpty() && this.validator.test(trimmed)) {
               parsed.add(trimmed);
            }
         }

         return this.apply(parsed);
      }

      private boolean apply(List<Object> parsed) {
         this.value = parsed;
         return true;
      }

      @Override
      void read(JsonElement element) {
         if (element.isJsonArray()) {
            List<Object> parsed = new ArrayList<>();

            for (JsonElement entry : element.getAsJsonArray()) {
               if (entry.isJsonPrimitive()) {
                  String string = entry.getAsString();
                  if (this.validator.test(string)) {
                     parsed.add(string);
                  }
               }
            }

            this.apply(parsed);
         }
      }

      @Override
      boolean readToml(String text) {
         String body = text.trim();
         if (body.startsWith("[")) {
            body = body.substring(1);
         }

         int close = body.lastIndexOf(93);
         if (close >= 0) {
            body = body.substring(0, close);
         }

         return this.setFromString(body);
      }

      @Override
      JsonElement write() {
         JsonArray array = new JsonArray();

         for (Object entry : this.value) {
            array.add(String.valueOf(entry));
         }

         return array;
      }
   }

   public static final class StringValue extends CodxSettings.ConfigValue<String> {
      StringValue(String name, String comment, String category, String defaultValue) {
         super(name, comment, category, defaultValue == null ? "" : defaultValue);
      }

      @Override
      public String typeName() {
         return "text";
      }

      @Override
      public boolean setFromString(String text) {
         this.value = CodxSettings.unquote(text);
         return true;
      }

      @Override
      void read(JsonElement element) {
         this.value = element.getAsString();
      }

      @Override
      boolean readToml(String text) {
         return this.setFromString(text);
      }

      @Override
      JsonElement write() {
         return new JsonPrimitive(this.value);
      }
   }
}
