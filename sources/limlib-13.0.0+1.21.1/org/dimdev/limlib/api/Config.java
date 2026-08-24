package org.dimdev.limlib.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Config {
   private static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .registerTypeAdapter((new TypeToken<ResourceKey<Level>>() {}).getType(), new Config.LevelKeyAdapter())
      .create();

   public static <T extends Config> T load(ISided<?> sided, Class<T> configClass) {
      return load(configClass, sided.configPath());
   }

   private static <T extends Config> T load(Class<T> configClass, Path configPath) {
      if (!Files.exists(configPath)) {
         T config = createInstance(configClass);
         save(config, configPath);
         return config;
      } else {
         try {
            Config var4;
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
               T config = (T)GSON.fromJson(reader, configClass);
               var4 = config == null ? createInstance(configClass) : config;
            }

            return (T)var4;
         } catch (JsonParseException | IOException var7) {
            throw new IllegalStateException("Failed to load " + configPath.getFileName() + " config from " + configPath, var7);
         }
      }
   }

   public static <T extends Config> T createInstance(Class<T> configClass) {
      try {
         Constructor<T> constructor = configClass.getDeclaredConstructor();
         constructor.setAccessible(true);
         return constructor.newInstance();
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException var2) {
         throw new IllegalStateException("Failed to create config instance for " + configClass.getName(), var2);
      }
   }

   public void save(ISided<?> sided) {
      save(this, sided.configPath());
   }

   private static void save(Config config, Path configPath) {
      try {
         Files.createDirectories(configPath.getParent());

         try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write(GSON.toJson(config));
         }
      } catch (IOException var7) {
         throw new IllegalStateException("Failed to save " + configPath.getFileName() + " to " + configPath, var7);
      }
   }

   public static final class LevelKeyAdapter implements JsonSerializer<ResourceKey<Level>>, JsonDeserializer<ResourceKey<Level>> {
      public ResourceKey<Level> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(json.getAsJsonPrimitive().getAsString()));
      }

      public JsonElement serialize(ResourceKey<Level> src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(src.location().toString());
      }
   }
}
