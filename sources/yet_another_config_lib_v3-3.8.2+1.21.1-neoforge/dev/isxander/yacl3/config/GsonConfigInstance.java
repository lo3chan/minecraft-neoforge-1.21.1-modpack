package dev.isxander.yacl3.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.isxander.yacl3.config.util.CodecSerializerAdapter;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.UnaryOperator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;

@Deprecated
public class GsonConfigInstance<T> extends ConfigInstance<T> {
   private final Gson gson;
   private final Path path;

   @Deprecated
   public GsonConfigInstance(Class<T> configClass, Path path) {
      this(configClass, path, new GsonBuilder());
   }

   @Deprecated
   public GsonConfigInstance(Class<T> configClass, Path path, Gson gson) {
      this(configClass, path, gson.newBuilder());
   }

   @Deprecated
   public GsonConfigInstance(Class<T> configClass, Path path, UnaryOperator<GsonBuilder> builder) {
      this(configClass, path, builder.apply(new GsonBuilder()));
   }

   @Deprecated
   public GsonConfigInstance(Class<T> configClass, Path path, GsonBuilder builder) {
      super(configClass);
      this.path = path;
      this.gson = builder.setExclusionStrategies(new ExclusionStrategy[]{new GsonConfigInstance.ConfigExclusionStrategy()})
         .registerTypeHierarchyAdapter(Component.class, new CodecSerializerAdapter(ComponentSerialization.CODEC))
         .registerTypeHierarchyAdapter(Style.class, new GsonConfigSerializer.StyleTypeAdapter())
         .registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
         .registerTypeHierarchyAdapter(Item.class, new GsonConfigInstance.ItemTypeAdapter())
         .serializeNulls()
         .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
         .create();
   }

   private GsonConfigInstance(Class<T> configClass, Path path, Gson gson, boolean fromBuilder) {
      super(configClass);
      this.path = path;
      this.gson = gson;
   }

   @Override
   public void save() {
      try {
         YACLConstants.LOGGER.info("Saving {}...", this.getConfigClass().getSimpleName());
         Files.writeString(this.path, this.gson.toJson(this.getConfig()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   @Override
   public void load() {
      try {
         if (Files.notExists(this.path)) {
            this.save();
            return;
         }

         YACLConstants.LOGGER.info("Loading {}...", this.getConfigClass().getSimpleName());
         this.setConfig((T)this.gson.fromJson(Files.readString(this.path), this.getConfigClass()));
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   public Path getPath() {
      return this.path;
   }

   public static <T> GsonConfigInstance.Builder<T> createBuilder(Class<T> configClass) {
      return new GsonConfigInstance.Builder<>(configClass);
   }

   public static class Builder<T> {
      private final Class<T> configClass;
      private Path path;
      private UnaryOperator<GsonBuilder> gsonBuilder = builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
         .serializeNulls()
         .registerTypeHierarchyAdapter(Component.class, new CodecSerializerAdapter(ComponentSerialization.CODEC))
         .registerTypeHierarchyAdapter(Style.class, new GsonConfigSerializer.StyleTypeAdapter())
         .registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
         .registerTypeHierarchyAdapter(Item.class, new GsonConfigInstance.ItemTypeAdapter());

      private Builder(Class<T> configClass) {
         this.configClass = configClass;
      }

      public GsonConfigInstance.Builder<T> setPath(Path path) {
         this.path = path;
         return this;
      }

      public GsonConfigInstance.Builder<T> overrideGsonBuilder(GsonBuilder gsonBuilder) {
         this.gsonBuilder = builder -> gsonBuilder;
         return this;
      }

      public GsonConfigInstance.Builder<T> overrideGsonBuilder(Gson gson) {
         return this.overrideGsonBuilder(gson.newBuilder());
      }

      public GsonConfigInstance.Builder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> gsonBuilder) {
         UnaryOperator<GsonBuilder> prev = this.gsonBuilder;
         this.gsonBuilder = builder -> gsonBuilder.apply(prev.apply(builder));
         return this;
      }

      public GsonConfigInstance<T> build() {
         UnaryOperator<GsonBuilder> gsonBuilder = builder -> this.gsonBuilder
            .apply(builder)
            .addSerializationExclusionStrategy(new GsonConfigInstance.ConfigExclusionStrategy())
            .addDeserializationExclusionStrategy(new GsonConfigInstance.ConfigExclusionStrategy());
         return new GsonConfigInstance<>(this.configClass, this.path, gsonBuilder.apply(new GsonBuilder()).create(), true);
      }
   }

   public static class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
      public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         return new Color(jsonElement.getAsInt(), true);
      }

      public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
         return new JsonPrimitive(color.getRGB());
      }
   }

   private static class ConfigExclusionStrategy implements ExclusionStrategy {
      public boolean shouldSkipField(FieldAttributes fieldAttributes) {
         return fieldAttributes.getAnnotation(ConfigEntry.class) == null;
      }

      public boolean shouldSkipClass(Class<?> aClass) {
         return false;
      }
   }

   public static class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
      public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         return ItemRegistryHelper.getItemFromName(jsonElement.getAsString());
      }

      public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
         return new JsonPrimitive(BuiltInRegistries.ITEM.getKey(item).toString());
      }
   }
}
