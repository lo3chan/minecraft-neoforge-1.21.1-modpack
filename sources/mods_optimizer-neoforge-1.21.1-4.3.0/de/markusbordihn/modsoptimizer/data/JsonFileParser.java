package de.markusbordihn.modsoptimizer.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import de.markusbordihn.modsoptimizer.Constants;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JsonFileParser {
   private JsonFileParser() {
   }

   public static JsonObject readJsonFile(JarFile jarFile, Path path) {
      ZipEntry modsFile = jarFile.getEntry(path.toString().replace("\\", "/"));
      if (modsFile != null && !modsFile.isDirectory()) {
         try {
            JsonObject var4;
            try (InputStream inputStream = jarFile.getInputStream(modsFile)) {
               var4 = parseJson(inputStream, path, jarFile);
            }

            return var4;
         } catch (Exception var8) {
            Constants.LOG.error("Error reading json file {} from {}: {}", new Object[]{path, jarFile, var8});
         }
      } else {
         Constants.LOG.error("Json file {} not found in {}", path.toString().replace("\\", "/"), jarFile);
      }

      return new JsonObject();
   }

   private static JsonObject parseJson(InputStream inputStream, Path path, JarFile jarFile) {
      try {
         JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));
         return jsonElement.getAsJsonObject();
      } catch (JsonSyntaxException var4) {
         Constants.LOG.warn("Invalid json file {} from {}:", new Object[]{path, jarFile, var4});
         return tryParsingWithLenient(inputStream, path, jarFile);
      } catch (Exception var5) {
         Constants.LOG.error("Error parsing json file {} from {}: {}", new Object[]{path, jarFile, var5});
         return new JsonObject();
      }
   }

   private static JsonObject tryParsingWithLenient(InputStream inputStream, Path path, JarFile jarFile) {
      try {
         JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
         reader.setLenient(true);
         JsonElement jsonElement = JsonParser.parseReader(reader);
         return jsonElement.getAsJsonObject();
      } catch (Exception var5) {
         Constants.LOG.error("Unable to parse invalid json file {} from {}: {}", new Object[]{path, jarFile, var5});
         return new JsonObject();
      }
   }
}
