package net.mehvahdjukaar.moonlight.core.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.mehvahdjukaar.codecui.Schema;
import net.mehvahdjukaar.codecui.SchemaCodecs;
import net.mehvahdjukaar.codecui.Schema.Bool;
import net.mehvahdjukaar.codecui.Schema.Color;
import net.mehvahdjukaar.codecui.Schema.DoubleRange;
import net.mehvahdjukaar.codecui.Schema.Enum;
import net.mehvahdjukaar.codecui.Schema.Field;
import net.mehvahdjukaar.codecui.Schema.FloatRange;
import net.mehvahdjukaar.codecui.Schema.IntRange;
import net.mehvahdjukaar.codecui.Schema.ListOf;
import net.mehvahdjukaar.codecui.Schema.LongRange;
import net.mehvahdjukaar.codecui.Schema.MapOf;
import net.mehvahdjukaar.codecui.Schema.OneOf;
import net.mehvahdjukaar.codecui.Schema.Record;
import net.mehvahdjukaar.codecui.Schema.ResourceId;
import net.mehvahdjukaar.codecui.Schema.Str;
import net.mehvahdjukaar.codecui.Schema.TagId;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigEditSession;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

final class SchemaForm {
   private static final Map<ResourceKey<? extends Registry<?>>, List<String>> ID_CACHE = new HashMap<>();
   final ConfigCategory root;
   final SchemaForm.Reader reader;

   private SchemaForm(ConfigCategory root, SchemaForm.Reader reader) {
      this.root = root;
      this.reader = reader;
   }

   static SchemaForm build(Component title, Schema<?> schema, JsonElement current, @Nullable JsonElement defaults) {
      if (schema instanceof Record<?> rec) {
         ConfigCategory root = new ConfigCategory(title);
         return new SchemaForm(root, populateRecord(root, rec, current, defaults));
      } else if (schema instanceof ListOf<?> list) {
         SchemaForm.ListCategory root = listCategory(title, list, current, defaults);
         return new SchemaForm(root, root.reader());
      } else {
         ConfigCategory root = new ConfigCategory(title);
         return new SchemaForm(root, buildField(root, "value", readable("value"), schema, current, defaults));
      }
   }

   private static SchemaForm.Reader buildField(
      ConfigCategory parent, String name, Component title, Schema<?> schema, @Nullable JsonElement current, @Nullable JsonElement def
   ) {
      JsonElement seed = current != null && !current.isJsonNull() ? current : def;

      return switch (schema) {
         case Bool ignored -> {
            boolean v = asBool(seed, false);
            ConfigOption.BooleanValue opt = new ConfigOption.BooleanValue(title, null, new MemoryConfigValue<>(v), v);
            parent.add(opt);
            yield s -> new JsonPrimitive(s.current(opt));
         }
         case IntRange r -> {
            int v = Math.clamp(asInt(seed, neutralInt(r.min(), r.max())), r.min(), r.max());
            ConfigOption.IntValue opt = new ConfigOption.IntValue(title, null, new MemoryConfigValue<>(v), v, r.min(), r.max());
            parent.add(opt);
            yield s -> new JsonPrimitive(s.current(opt));
         }
         case FloatRange rx -> {
            float v = Math.clamp(asFloat(seed, neutralFloat(rx.min(), rx.max())), rx.min(), rx.max());
            ConfigOption.FloatValue opt = new ConfigOption.FloatValue(title, null, new MemoryConfigValue<>(v), v, rx.min(), rx.max());
            parent.add(opt);
            yield s -> new JsonPrimitive(s.current(opt));
         }
         case DoubleRange rxx -> {
            double v = Math.clamp(asDouble(seed, neutralDouble(rxx.min(), rxx.max())), rxx.min(), rxx.max());
            ConfigOption.DoubleValue opt = new ConfigOption.DoubleValue(title, null, new MemoryConfigValue<>(v), v, rxx.min(), rxx.max());
            parent.add(opt);
            yield s -> new JsonPrimitive(s.current(opt));
         }
         case LongRange rxxx -> {
            long v = Math.clamp(asLong(seed, 0L), rxxx.min(), rxxx.max());
            String sv = Long.toString(v);
            Predicate<Object> valid = o -> o instanceof String strx && isLongInRange(strx, r.min(), r.max());
            ConfigOption.StringValue opt = new ConfigOption.StringValue(title, null, new MemoryConfigValue<>(sv), sv, valid);
            parent.add(opt);
            yield s -> new JsonPrimitive(parseLongOr(s.current(opt), v));
         }
         case Color c -> {
            int rgb = asColor(seed, -1);
            ConfigOption.ColorValue opt = new ConfigOption.ColorValue(title, null, new MemoryConfigValue<>(rgb), rgb, c.hasAlpha());
            parent.add(opt);
            yield s -> {
               int col = s.current(opt);
               return c.hexString() ? new JsonPrimitive(ColorUtils.toHexString(col, c.hasAlpha())) : new JsonPrimitive(col);
            };
         }
         case Str str -> {
            String v = asString(seed, "");
            Predicate<Object> valid = o -> o instanceof String x
               && x.length() >= str.minLen()
               && x.length() <= str.maxLen()
               && (str.pattern() == null || str.pattern().matcher(x).matches());
            ConfigOption.StringValue opt = new ConfigOption.StringValue(title, null, new MemoryConfigValue<>(v), v, valid);
            parent.add(opt);
            yield s -> new JsonPrimitive(s.current(opt));
         }
         case ResourceId id -> idField(
            parent,
            title,
            registryIds(id.registry()),
            asString(seed, ""),
            o -> o instanceof String x && ResourceLocation.tryParse(x) != null,
            UnaryOperator.identity(),
            iconsFor(id.registry())
         );
         case TagId tag -> {
            List<String> tags = SchemaCodecs.availableTagIds(tag.registry()).stream().map(t -> normalizeTagId(t.toString(), tag.hashed())).toList();
            yield idField(parent, title, tags, asString(seed, ""), o -> o instanceof String x && isTagId(x), s -> normalizeTagId(s, tag.hashed()), null);
         }
         case Enum<?> en -> enumField(parent, title, en, seed);
         case Record<?> rec -> {
            ConfigCategory sub = new ConfigCategory(title);
            parent.add(sub);
            yield populateRecord(sub, rec, current, def);
         }
         case ListOf<?> list -> {
            SchemaForm.ListCategory sub = listCategory(title, list, seed, def);
            parent.add(sub);
            yield sub.reader();
         }
         default -> rawJsonField(parent, title, schema, seed);
      };
   }

   private static SchemaForm.Reader populateRecord(ConfigCategory cat, Record<?> rec, @Nullable JsonElement current, @Nullable JsonElement def) {
      JsonObject cur = current instanceof JsonObject o ? o : null;
      JsonObject dfl = def instanceof JsonObject ox ? ox : null;
      List<SchemaForm.FieldReader> fields = new ArrayList<>(rec.fields().size());

      for (Field<?, ?> f : rec.fields()) {
         JsonElement fc = cur != null ? cur.get(f.name()) : null;
         JsonElement fd = dfl != null ? dfl.get(f.name()) : null;
         SchemaForm.Reader r = buildField(cat, f.name(), readable(f.name()), f.schema(), fc, fd);
         fields.add(new SchemaForm.FieldReader(f.name(), r));
      }

      return s -> {
         JsonObject oxx = new JsonObject();

         for (SchemaForm.FieldReader fr : fields) {
            JsonElement v = fr.reader.read(s);
            if (v != null) {
               oxx.add(fr.name, v);
            }
         }

         return oxx;
      };
   }

   private static SchemaForm.ListCategory listCategory(Component title, ListOf<?> list, @Nullable JsonElement current, @Nullable JsonElement def) {
      JsonElement template = def instanceof JsonArray a && !a.isEmpty() ? a.get(0) : emptyFor(list.element());
      SchemaForm.ListCategory cat = new SchemaForm.ListCategory(title, list.element(), template, list.min(), list.max());
      List<JsonElement> values = new ArrayList<>();
      if (current instanceof JsonArray ax) {
         ax.forEach(values::add);
      }

      cat.setEntries(values);
      return cat;
   }

   private static SchemaForm.Reader idField(
      ConfigCategory parent,
      Component title,
      List<String> known,
      String current,
      Predicate<Object> valid,
      UnaryOperator<String> normalize,
      @Nullable Function<String, ItemStack> icon
   ) {
      if (known.isEmpty()) {
         ConfigOption.StringValue opt = new ConfigOption.StringValue(title, null, new MemoryConfigValue<>(current), current, valid);
         parent.add(opt);
         return s -> new JsonPrimitive(normalize.apply(s.current(opt)));
      } else {
         List<String> options = known.contains(current) ? known : Stream.concat(Stream.of(current), known.stream()).toList();
         ConfigOption.DropdownValue opt = new ConfigOption.DropdownValue(title, null, new MemoryConfigValue<>(current), current, () -> options, icon);
         parent.add(opt);
         return s -> new JsonPrimitive(normalize.apply(s.current(opt)));
      }
   }

   @Nullable
   private static Function<String, ItemStack> iconsFor(@Nullable ResourceKey<? extends Registry<?>> registry) {
      return !Registries.ITEM.equals(registry) && !Registries.BLOCK.equals(registry) ? null : id -> ConfigScreenIcons.resolve(ResourceLocation.tryParse(id));
   }

   private static List<String> registryIds(@Nullable ResourceKey<? extends Registry<?>> key) {
      if (key == null) {
         return List.of();
      } else {
         List<String> cached = ID_CACHE.get(key);
         if (cached != null) {
            return cached;
         } else {
            Registry<?> builtIn = (Registry<?>)BuiltInRegistries.REGISTRY.get(key.location());
            Registry<?> registry = builtIn != null ? builtIn : dynamicRegistry(key);
            if (registry == null) {
               return List.of();
            } else {
               List<String> ids = registry.keySet().stream().<String>map(ResourceLocation::toString).sorted().toList();
               if (builtIn != null) {
                  ID_CACHE.put(key, ids);
               }

               return ids;
            }
         }
      }
   }

   @Nullable
   private static Registry<?> dynamicRegistry(ResourceKey<? extends Registry<?>> key) {
      try {
         return (Registry<?>)Utils.hackyGetRegistryAccess().registry(key).orElse(null);
      } catch (Exception var2) {
         return null;
      }
   }

   private static SchemaForm.Reader enumField(ConfigCategory parent, Component title, Enum<?> en, @Nullable JsonElement seed) {
      List<String> labels = labelsOf(en);
      String initial = asString(seed, labels.isEmpty() ? "" : (String)labels.getFirst());
      if (!labels.contains(initial)) {
         initial = labels.isEmpty() ? "" : (String)labels.getFirst();
      }

      ConfigOption.DropdownValue opt = new ConfigOption.DropdownValue(title, null, new MemoryConfigValue<>(initial), initial, () -> labels, null);
      parent.add(opt);
      return s -> new JsonPrimitive(s.current(opt));
   }

   private static List<String> labelsOf(Enum<?> en) {
      Enum<Object> e = (Enum<Object>)en;
      List<String> out = new ArrayList<>(en.options().size());

      for (Object o : en.options()) {
         out.add((String)e.label().apply(o));
      }

      return out;
   }

   private static SchemaForm.Reader rawJsonField(ConfigCategory parent, Component title, Schema<?> schema, @Nullable JsonElement seed) {
      JsonElement node = seed != null ? seed : emptyFor(schema);
      ConfigOption.JsonValue opt = new ConfigOption.JsonValue(title, null, () -> node);
      parent.add(opt);
      return s -> {
         if (s.current(opt) instanceof String str) {
            try {
               return JsonParser.parseString(str);
            } catch (Exception var6) {
               return node;
            }
         } else {
            return node;
         }
      };
   }

   private static Component readable(String name) {
      return Component.literal(TextHelper.getReadableName(name));
   }

   private static boolean asBool(@Nullable JsonElement e, boolean fallback) {
      return isPrim(e) ? e.getAsBoolean() : fallback;
   }

   private static int asInt(@Nullable JsonElement e, int fallback) {
      try {
         return isNumber(e) ? e.getAsInt() : fallback;
      } catch (Exception var3) {
         return fallback;
      }
   }

   private static long asLong(@Nullable JsonElement e, long fallback) {
      try {
         return isNumber(e) ? e.getAsLong() : fallback;
      } catch (Exception var4) {
         return fallback;
      }
   }

   private static float asFloat(@Nullable JsonElement e, float fallback) {
      try {
         return isNumber(e) ? e.getAsFloat() : fallback;
      } catch (Exception var3) {
         return fallback;
      }
   }

   private static double asDouble(@Nullable JsonElement e, double fallback) {
      try {
         return isNumber(e) ? e.getAsDouble() : fallback;
      } catch (Exception var4) {
         return fallback;
      }
   }

   private static String asString(@Nullable JsonElement e, String fallback) {
      return isPrim(e) ? e.getAsString() : fallback;
   }

   private static int asColor(@Nullable JsonElement e, int fallback) {
      if (!isPrim(e)) {
         return fallback;
      } else {
         JsonPrimitive p = e.getAsJsonPrimitive();
         if (p.isNumber()) {
            return p.getAsInt();
         } else if (p.isString()) {
            try {
               return ColorUtils.parseHex(p.getAsString().trim());
            } catch (Exception var4) {
               return fallback;
            }
         } else {
            return fallback;
         }
      }
   }

   private static String stripHash(String s) {
      return s.startsWith("#") ? s.substring(1) : s;
   }

   private static boolean isTagId(String s) {
      return ResourceLocation.tryParse(stripHash(s)) != null;
   }

   private static String normalizeTagId(String s, boolean hashed) {
      return hashed ? "#" + stripHash(s) : stripHash(s);
   }

   private static boolean isPrim(@Nullable JsonElement e) {
      return e != null && e.isJsonPrimitive();
   }

   private static boolean isNumber(@Nullable JsonElement e) {
      return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber();
   }

   private static JsonElement emptyFor(Schema<?> schema) {
      return (JsonElement)(switch (schema) {
         case ListOf<?> ignored -> new JsonArray();
         case MapOf<?, ?> ignoredx -> new JsonObject();
         case OneOf<?> ignoredxx -> new JsonObject();
         case Record<?> ignoredxxx -> new JsonObject();
         default -> JsonNull.INSTANCE;
      });
   }

   private static int neutralInt(int min, int max) {
      return Math.clamp(0L, min, max);
   }

   private static float neutralFloat(float min, float max) {
      return Math.clamp(0.0F, min, max);
   }

   private static double neutralDouble(double min, double max) {
      return Math.clamp(0.0, min, max);
   }

   private static boolean isLongInRange(String s, long min, long max) {
      try {
         long v = Long.parseLong(s.trim());
         return v >= min && v <= max;
      } catch (Exception var7) {
         return false;
      }
   }

   private static long parseLongOr(String s, long fallback) {
      try {
         return Long.parseLong(s.trim());
      } catch (Exception var4) {
         return fallback;
      }
   }

   private record FieldReader(String name, SchemaForm.Reader reader) {
   }

   static final class ListCategory extends ConfigCategory {
      private final Schema<?> element;
      private final JsonElement template;
      private final int min;
      private final int max;
      private final List<SchemaForm.Reader> readers = new ArrayList<>();

      private ListCategory(Component title, Schema<?> element, JsonElement template, int min, int max) {
         super(title);
         this.element = element;
         this.template = template;
         this.min = min;
         this.max = max;
      }

      SchemaForm.Reader reader() {
         return s -> {
            JsonArray array = new JsonArray();

            for (SchemaForm.Reader r : this.readers) {
               array.add(r.read(s));
            }

            return array;
         };
      }

      List<JsonElement> snapshot(ConfigEditSession session) {
         List<JsonElement> out = new ArrayList<>(this.readers.size());

         for (SchemaForm.Reader r : this.readers) {
            out.add(r.read(session));
         }

         return out;
      }

      void setEntries(List<JsonElement> values) {
         this.clear();
         this.readers.clear();

         for (int i = 0; i < values.size(); i++) {
            this.readers.add(SchemaForm.buildField(this, "entry" + i, entryTitle(i), this.element, values.get(i), this.template));
         }
      }

      JsonElement newEntry() {
         return this.template.deepCopy();
      }

      boolean canAdd() {
         return this.readers.size() < this.max;
      }

      boolean canRemove() {
         return this.readers.size() > this.min;
      }

      private static Component entryTitle(int index) {
         return Component.translatable("gui.moonlight.config.list_entry", new Object[]{index + 1});
      }
   }

   @FunctionalInterface
   interface Reader {
      JsonElement read(ConfigEditSession var1);
   }
}
