package dev.isxander.yacl3.config.v2.impl.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.JsonOps;
import dev.isxander.yacl3.config.util.CodecSerializerAdapter;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.SerialField;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Style.Serializer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;

public class GsonConfigSerializer<T> extends ConfigSerializer<T> {
   private final Gson gson;
   private final Path path;
   private final boolean json5;

   private GsonConfigSerializer(ConfigClassHandler<T> config, Path path, Gson gson, boolean json5) {
      super(config);
      this.gson = gson;
      this.path = path;
      this.json5 = json5;
   }

   @Override
   public void save() {
      YACLConstants.LOGGER.info("Serializing {} to '{}'", this.config.configClass(), this.path);

      try (StringWriter stringWriter = new StringWriter()) {
         JsonWriter jsonWriter = this.json5 ? JsonWriter.json5(stringWriter) : JsonWriter.json(stringWriter);
         GsonWriter gsonWriter = new GsonWriter(jsonWriter);
         jsonWriter.beginObject();

         for (ConfigField<?> field : this.config.fields()) {
            SerialField serial = field.serial().orElse(null);
            if (serial != null) {
               if (!this.json5 && serial.comment().isPresent() && YACLPlatform.isDevelopmentEnv()) {
                  YACLConstants.LOGGER
                     .warn(
                        "Found comment in config field '{}', but json5 is not enabled. Enable it with `.setJson5(true)` on the `GsonConfigSerializerBuilder`. Comments will not be serialized. This warning is only visible in development environments.",
                        serial.serialName()
                     );
               }

               jsonWriter.comment(serial.comment().orElse(null));
               jsonWriter.name(serial.serialName());

               JsonElement element;
               try {
                  element = this.gson.toJsonTree(field.access().get(), field.access().type());
               } catch (Exception var13) {
                  YACLConstants.LOGGER.error("Failed to serialize config field '{}'. Serializing as null.", serial.serialName(), var13);
                  jsonWriter.nullValue();
                  continue;
               }

               try {
                  this.gson.toJson(element, gsonWriter);
               } catch (Exception var12) {
                  YACLConstants.LOGGER
                     .error(
                        "Failed to serialize config field '{}'. Due to the error state this JSON writer cannot continue safely and the save will be abandoned.",
                        serial.serialName(),
                        var12
                     );
                  return;
               }
            }
         }

         jsonWriter.endObject();
         jsonWriter.flush();
         Files.createDirectories(this.path.getParent());
         Files.writeString(this.path, stringWriter.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
      } catch (IOException var15) {
         YACLConstants.LOGGER.error("Failed to serialize config class '{}'.", this.config.configClass().getSimpleName(), var15);
      }
   }

   @Override
   public ConfigSerializer.LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
      if (!Files.exists(this.path)) {
         YACLConstants.LOGGER.info("Config file '{}' does not exist. Creating it with default values.", this.path);
         this.save();
         return ConfigSerializer.LoadResult.NO_CHANGE;
      } else {
         YACLConstants.LOGGER.info("Deserializing {} from '{}'", this.config.configClass().getSimpleName(), this.path);
         Map<String, ConfigField<?>> fieldMap = Arrays.stream(this.config.fields())
            .filter(fieldx -> fieldx.serial().isPresent())
            .collect(Collectors.toMap(f -> f.serial().orElseThrow().serialName(), Function.identity()));
         Set<String> missingFields = fieldMap.keySet();
         boolean dirty = false;

         try {
            JsonReader jsonReader = this.json5 ? JsonReader.json5(this.path) : JsonReader.json(this.path);

            label105: {
               ConfigSerializer.LoadResult var13;
               try {
                  GsonReader gsonReader = new GsonReader(jsonReader);
                  jsonReader.beginObject();

                  while (true) {
                     if (!jsonReader.hasNext()) {
                        jsonReader.endObject();
                        break label105;
                     }

                     String name = jsonReader.nextName();
                     ConfigField<?> field = fieldMap.get(name);
                     missingFields.remove(name);
                     if (field == null) {
                        YACLConstants.LOGGER.warn("Found unknown config field '{}'.", name);
                        jsonReader.skipValue();
                     } else {
                        FieldAccess<?> bufferAccess = bufferAccessMap.get(field);
                        SerialField serial = field.serial().orElse(null);
                        if (serial != null) {
                           JsonElement element;
                           try {
                              element = (JsonElement)this.gson.fromJson(gsonReader, JsonElement.class);
                           } catch (Exception var16) {
                              YACLConstants.LOGGER
                                 .error(
                                    "Failed to deserialize config field '{}'. Due to the error state this JSON reader cannot be re-used and loading will be aborted.",
                                    name,
                                    var16
                                 );
                              var13 = ConfigSerializer.LoadResult.FAILURE;
                              break;
                           }

                           if (element.isJsonNull() && !serial.nullable()) {
                              YACLConstants.LOGGER
                                 .warn("Found null value in non-nullable config field '{}'. Leaving field as default and marking as dirty.", name);
                              dirty = true;
                           } else {
                              try {
                                 ((FieldAccess<Object>)bufferAccess).set(this.gson.fromJson(element, bufferAccess.type()));
                              } catch (Exception var15) {
                                 YACLConstants.LOGGER.error("Failed to deserialize config field '{}'. Leaving as default.", name, var15);
                              }
                           }
                        }
                     }
                  }
               } catch (Throwable var17) {
                  if (jsonReader != null) {
                     try {
                        jsonReader.close();
                     } catch (Throwable var14) {
                        var17.addSuppressed(var14);
                     }
                  }

                  throw var17;
               }

               if (jsonReader != null) {
                  jsonReader.close();
               }

               return var13;
            }

            if (jsonReader != null) {
               jsonReader.close();
            }
         } catch (IOException var18) {
            YACLConstants.LOGGER.error("Failed to deserialize config class.", var18);
            return ConfigSerializer.LoadResult.FAILURE;
         }

         if (!missingFields.isEmpty()) {
            for (String missingField : missingFields) {
               if (fieldMap.get(missingField).serial().orElseThrow().required()) {
                  dirty = true;
                  YACLConstants.LOGGER.warn("Missing required config field '{}''. Re-saving as default.", missingField);
               }
            }
         }

         return dirty ? ConfigSerializer.LoadResult.DIRTY : ConfigSerializer.LoadResult.SUCCESS;
      }
   }

   @Deprecated
   @Override
   public void load() {
      YACLConstants.LOGGER.warn("Calling ConfigSerializer#load() directly is deprecated. Please use ConfigClassHandler#load() instead.");
      this.config.load();
   }

   @Internal
   public static class Builder<T> implements GsonConfigSerializerBuilder<T> {
      private final ConfigClassHandler<T> config;
      private Path path;
      private boolean json5;
      private UnaryOperator<GsonBuilder> gsonBuilder = builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
         .serializeNulls()
         .registerTypeHierarchyAdapter(Component.class, new CodecSerializerAdapter(ComponentSerialization.CODEC))
         .registerTypeHierarchyAdapter(Style.class, new GsonConfigSerializer.StyleTypeAdapter())
         .registerTypeHierarchyAdapter(Color.class, new GsonConfigSerializer.ColorTypeAdapter())
         .registerTypeHierarchyAdapter(Item.class, new GsonConfigSerializer.ItemTypeAdapter())
         .setPrettyPrinting();

      public Builder(ConfigClassHandler<T> config) {
         this.config = config;
      }

      public GsonConfigSerializer.Builder<T> setPath(Path path) {
         this.path = path;
         return this;
      }

      public GsonConfigSerializer.Builder<T> overrideGsonBuilder(GsonBuilder gsonBuilder) {
         this.gsonBuilder = builder -> gsonBuilder;
         return this;
      }

      public GsonConfigSerializer.Builder<T> overrideGsonBuilder(Gson gson) {
         return this.overrideGsonBuilder(gson.newBuilder());
      }

      public GsonConfigSerializer.Builder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> gsonBuilder) {
         UnaryOperator<GsonBuilder> prev = this.gsonBuilder;
         this.gsonBuilder = builder -> gsonBuilder.apply(prev.apply(builder));
         return this;
      }

      public GsonConfigSerializer.Builder<T> setJson5(boolean json5) {
         this.json5 = json5;
         return this;
      }

      public GsonConfigSerializer<T> build() {
         return new GsonConfigSerializer<>(this.config, this.path, this.gsonBuilder.apply(new GsonBuilder()).create(), this.json5);
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

   public static class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
      public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         return ItemRegistryHelper.getItemFromName(jsonElement.getAsString());
      }

      public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
         return new JsonPrimitive(BuiltInRegistries.ITEM.getKey(item).toString());
      }
   }

   public static class StyleTypeAdapter implements JsonSerializer<Style>, JsonDeserializer<Style> {
      public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         return Serializer.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(Style.EMPTY);
      }

      public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
         return (JsonElement)Serializer.CODEC.encodeStart(JsonOps.INSTANCE, src).result().orElse(JsonNull.INSTANCE);
      }
   }
}
