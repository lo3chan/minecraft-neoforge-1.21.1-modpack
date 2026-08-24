package fuzs.puzzleslib.api.config.v3.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.GsonHelper;

public final class GsonCodecHelper {
   public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private GsonCodecHelper() {
   }

   public static <T> boolean saveIfAbsent(Codec<T> codec, Supplier<T> value, Path path) {
      return !path.toFile().exists() && save(codec, value.get(), path);
   }

   public static <T> boolean save(Codec<T> codec, T value, Path path) {
      path.toFile().getParentFile().mkdirs();

      try {
         JsonWriter jsonWriter = new JsonWriter(new FileWriter(path.toFile()));

         boolean var5;
         try {
            jsonWriter.setSerializeNulls(false);
            jsonWriter.setIndent("  ");
            JsonElement jsonElement = (JsonElement)codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
            GsonHelper.writeValue(jsonWriter, jsonElement, null);
            var5 = true;
         } catch (Throwable var7) {
            try {
               jsonWriter.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         jsonWriter.close();
         return var5;
      } catch (Exception var8) {
         PuzzlesLib.LOGGER.error("Failed to write file at {}", path, var8);
         return false;
      }
   }

   public static <T> T load(Codec<T> codec, Supplier<T> value, Path path) {
      saveIfAbsent(codec, value, path);
      return load(codec, path).orElseGet(value);
   }

   public static <T> Optional<T> load(Codec<T> codec, Path path) {
      try {
         Optional var4;
         try (FileReader fileReader = new FileReader(path.toFile())) {
            JsonElement jsonElement = (JsonElement)GsonHelper.fromJson(GSON, fileReader, JsonElement.class);
            var4 = codec.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial();
         }

         return var4;
      } catch (Exception var7) {
         PuzzlesLib.LOGGER.error("Failed to read file at {}", path, var7);
         return Optional.empty();
      }
   }
}
