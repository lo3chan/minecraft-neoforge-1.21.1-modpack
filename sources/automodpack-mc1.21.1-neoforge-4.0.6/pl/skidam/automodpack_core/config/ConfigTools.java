package pl.skidam.automodpack_core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.utils.AddressHelpers;

public class ConfigTools {
   public static Gson GSON = new GsonBuilder()
      .disableHtmlEscaping()
      .setPrettyPrinting()
      .registerTypeAdapter(InetSocketAddress.class, new ConfigTools.InetSocketAddressTypeAdapter())
      .create();

   public static <T> T getConfigObject(Class<T> configClass) {
      T object = null;

      try {
         object = configClass.getConstructor().newInstance();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return object;
   }

   public static <T> T softLoad(Path configFile, Class<T> configClass) {
      try {
         if (Files.isRegularFile(configFile)) {
            String json = Files.readString(configFile);
            return (T)GSON.fromJson(json, configClass);
         }
      } catch (Exception var3) {
      }

      return null;
   }

   public static <T> T load(Path configFile, Class<T> configClass) {
      try {
         if (!Files.isDirectory(configFile.getParent())) {
            Files.createDirectories(configFile.getParent());
         }

         if (Files.isRegularFile(configFile)) {
            String json = Files.readString(configFile);
            T obj = (T)GSON.fromJson(json, configClass);
            if (obj == null) {
               GlobalVariables.LOGGER.error("Parsed object is null. Possible JSON syntax error in file: " + configFile);
               return null;
            }

            save(configFile, obj);
            return obj;
         }
      } catch (JsonSyntaxException var5) {
         GlobalVariables.LOGGER.error("JSON syntax error while loading config! {} {}", configClass, var5.getMessage());
         GlobalVariables.LOGGER
            .error(
               "This error most often happens when you e.g. forget to put a comma between fields in JSON file. Check the file: "
                  + configFile.toAbsolutePath().normalize()
            );
         return null;
      } catch (Exception var6) {
         GlobalVariables.LOGGER.error("Couldn't load config! " + configClass);
         var6.printStackTrace();
      }

      try {
         T obj = getConfigObject(configClass);
         save(configFile, obj);
         return obj;
      } catch (Exception var4) {
         GlobalVariables.LOGGER.error("Invalid config class! " + configClass);
         var4.printStackTrace();
         return null;
      }
   }

   public static void save(Path configFile, Object configObject) {
      try {
         if (!Files.isDirectory(configFile.getParent())) {
            Files.createDirectories(configFile.getParent());
         }

         Files.writeString(configFile, GSON.toJson(configObject), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      } catch (Exception var3) {
         GlobalVariables.LOGGER.error("Couldn't save config! " + configObject.getClass());
         var3.printStackTrace();
      }
   }

   public static Jsons.ModpackContentFields loadModpackContent(Path modpackContentFile) {
      try {
         if (Files.isRegularFile(modpackContentFile)) {
            String json = Files.readString(modpackContentFile);
            return (Jsons.ModpackContentFields)GSON.fromJson(json, Jsons.ModpackContentFields.class);
         }
      } catch (Exception var2) {
         GlobalVariables.LOGGER.error("Couldn't load modpack content! {}", modpackContentFile.toAbsolutePath().normalize(), var2);
      }

      return null;
   }

   public static void saveModpackContent(Path modpackContentFile, Jsons.ModpackContentFields configObject) {
      try {
         if (!Files.isDirectory(modpackContentFile.getParent())) {
            Files.createDirectories(modpackContentFile.getParent());
         }

         Files.writeString(modpackContentFile, GSON.toJson(configObject), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      } catch (Exception var3) {
         GlobalVariables.LOGGER.error("Couldn't save modpack content! " + configObject.getClass());
         var3.printStackTrace();
      }
   }

   private static class InetSocketAddressTypeAdapter implements JsonSerializer<InetSocketAddress>, JsonDeserializer<InetSocketAddress> {
      public JsonElement serialize(InetSocketAddress src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(src.getHostString() + ":" + src.getPort());
      }

      public InetSocketAddress deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         String address = json.getAsString();
         return AddressHelpers.parse(address);
      }
   }
}
