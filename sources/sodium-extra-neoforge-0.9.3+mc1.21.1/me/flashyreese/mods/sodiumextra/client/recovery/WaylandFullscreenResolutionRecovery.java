package me.flashyreese.mods.sodiumextra.client.recovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import me.flashyreese.mods.sodiumextra.client.config.ConfigFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WaylandFullscreenResolutionRecovery {
   private static final Logger LOGGER = LoggerFactory.getLogger("Sodium Extra Recovery");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final String FULLSCREEN = "fullscreen:";
   private static final String FULLSCREEN_RESOLUTION = "fullscreenResolution:";

   public static void recoverIfNeeded(Path gameDirectory, Path configDirectory) {
      if (isWaylandSession()) {
         Path configFile = configDirectory.resolve("sodium-extra-options.json");
         JsonObject root = readJsonObject(configFile);
         if (root != null) {
            JsonObject extraSettings = getObject(root, "extra_settings");
            if (extraSettings != null && readBoolean(extraSettings, "wayland_fullscreen_resolution_recovery_pending")) {
               extraSettings.addProperty("wayland_fullscreen_resolution", false);
               extraSettings.addProperty("wayland_fullscreen_resolution_recovery_pending", false);
               writeJson(configFile, root);
               boolean changedOptions = recoverMinecraftOptions(gameDirectory.resolve("options.txt"));
               LOGGER.warn(
                  "Recovered from a pending experimental Wayland/XWayland fullscreen resolution change{}",
                  changedOptions ? " and reset Minecraft fullscreen state" : ""
               );
            }
         }
      }
   }

   private static boolean recoverMinecraftOptions(Path optionsFile) {
      if (!Files.exists(optionsFile)) {
         return false;
      } else {
         try {
            List<String> input = Files.readAllLines(optionsFile, StandardCharsets.UTF_8);
            List<String> output = new ArrayList<>(input.size());
            boolean changed = false;
            boolean sawFullscreen = false;

            for (String line : input) {
               if (line.startsWith("fullscreenResolution:")) {
                  changed = true;
               } else if (line.startsWith("fullscreen:")) {
                  sawFullscreen = true;
                  if (!line.equals("fullscreen:false")) {
                     changed = true;
                  }

                  output.add("fullscreen:false");
               } else {
                  output.add(line);
               }
            }

            if (!sawFullscreen) {
               output.add("fullscreen:false");
               changed = true;
            }

            if (changed) {
               ConfigFileIO.writeLinesAtomically(optionsFile, output);
            }

            return changed;
         } catch (IOException var7) {
            LOGGER.error("Failed to recover Minecraft fullscreen options", var7);
            return false;
         }
      }
   }

   private static JsonObject readJsonObject(Path path) {
      if (!Files.exists(path)) {
         return null;
      } else {
         try {
            JsonElement element = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            return element.isJsonObject() ? element.getAsJsonObject() : null;
         } catch (Exception var2) {
            LOGGER.error("Failed to read Sodium Extra options for fullscreen recovery", var2);
            return null;
         }
      }
   }

   private static void writeJson(Path path, JsonObject object) {
      try {
         ConfigFileIO.writeStringAtomically(path, GSON.toJson(object) + System.lineSeparator());
      } catch (IOException var3) {
         LOGGER.error("Failed to write Sodium Extra fullscreen recovery state", var3);
      }
   }

   private static JsonObject getObject(JsonObject object, String key) {
      JsonElement element = object.get(key);
      return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
   }

   private static boolean readBoolean(JsonObject object, String key) {
      JsonElement element = object.get(key);
      return element != null && element.isJsonPrimitive() && element.getAsBoolean();
   }

   private static boolean isWaylandSession() {
      String sessionType = System.getenv("XDG_SESSION_TYPE");
      return System.getenv("WAYLAND_DISPLAY") != null || "wayland".equalsIgnoreCase(sessionType);
   }
}
