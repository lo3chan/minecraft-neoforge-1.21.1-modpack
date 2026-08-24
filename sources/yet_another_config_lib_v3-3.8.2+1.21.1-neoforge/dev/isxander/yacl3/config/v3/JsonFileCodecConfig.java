package dev.isxander.yacl3.config.v3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class JsonFileCodecConfig<T extends JsonFileCodecConfig<T>> extends CodecConfig<T> {
   private final Path configPath;
   private final Gson gson;

   public JsonFileCodecConfig(Path configPath) {
      this.configPath = configPath;
      this.gson = this.createGson();
   }

   public void saveToFile() {
      DataResult<JsonElement> jsonTreeResult = this.encodeStart(JsonOps.INSTANCE);
      if (jsonTreeResult.error().isPresent()) {
         this.onSaveError(
            JsonFileCodecConfig.SaveError.ENCODING, new IllegalStateException("Failed to encode: " + ((Error)jsonTreeResult.error().get()).message())
         );
      } else {
         JsonElement jsonTree = (JsonElement)jsonTreeResult.result().orElseThrow();
         String json = this.gson.toJson(jsonTree);

         try {
            Files.writeString(this.configPath, json, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
         } catch (IOException var5) {
            this.onSaveError(JsonFileCodecConfig.SaveError.WRITING, var5);
         }
      }
   }

   public boolean loadFromFile() {
      if (Files.notExists(this.configPath)) {
         return false;
      } else {
         String json;
         try {
            json = Files.readString(this.configPath);
         } catch (IOException var5) {
            this.onLoadError(JsonFileCodecConfig.LoadError.READING, var5);
            return false;
         }

         JsonElement jsonTree;
         try {
            jsonTree = JsonParser.parseString(json);
         } catch (JsonParseException var4) {
            this.onLoadError(JsonFileCodecConfig.LoadError.JSON_PARSING, var4);
            return false;
         }

         return this.decode(jsonTree, JsonOps.INSTANCE);
      }
   }

   protected Gson createGson() {
      return new GsonBuilder().setPrettyPrinting().create();
   }

   protected void onSaveError(JsonFileCodecConfig.SaveError error, @Nullable Throwable e) {
      throw new IllegalStateException("Error whilst " + error.name().toLowerCase(), e);
   }

   protected void onLoadError(JsonFileCodecConfig.LoadError error, @Nullable Throwable e) {
      throw new IllegalStateException("Error whilst " + error.name().toLowerCase(), e);
   }

   protected static enum LoadError {
      READING,
      JSON_PARSING,
      DECODING;
   }

   protected static enum SaveError {
      WRITING,
      ENCODING;
   }
}
