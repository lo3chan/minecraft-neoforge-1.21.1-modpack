package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;

public class BaseProperties {
   private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
   private final Path path;
   private final String name;
   protected JsonObject properties;
   private boolean writeProperties;

   public BaseProperties(Path path, String name) {
      this.path = path;
      this.name = name;
      this.properties = new JsonObject();
      this.writeProperties = false;
      if (Files.exists(path)) {
         try (BufferedReader reader = Files.newBufferedReader(path)) {
            this.properties = (JsonObject)GSON.fromJson(reader, JsonObject.class);
         } catch (JsonIOException | IOException var8) {
            CrashReport crashreport = CrashReport.forThrowable(var8, "[KubeJS] Loading settings");
            CrashReportCategory crashreportcategory = crashreport.addCategory("File being loaded");
            crashreportcategory.setDetail("Name", () -> name);
            crashreportcategory.setDetail("File Path", path::toString);
            crashreportcategory.setDetail("Readable?", () -> String.valueOf(Files.isReadable(path)));
            crashreportcategory.setDetail("Writable?", () -> String.valueOf(Files.isWritable(path)));
            crashreportcategory.setDetail("Executable?", () -> String.valueOf(Files.isExecutable(path)));
            throw new ReportedException(crashreport);
         } catch (JsonSyntaxException var9) {
            ConsoleJS.STARTUP
               .error("Error parsing properties JSON for file %s! Default settings will be loaded, and changes won't be saved!".formatted(path), var9);
         }
      } else {
         this.writeProperties = true;
      }

      this.load();
      if (this.writeProperties) {
         this.save();
      }
   }

   protected void load() {
   }

   public void remove(String key) {
      if (this.properties.remove(key) != null) {
         this.writeProperties = true;
      }
   }

   public JsonElement get(String key) {
      JsonElement e = this.properties.get(key);
      return (JsonElement)(e == null ? JsonNull.INSTANCE : e);
   }

   public String get(String key, String def) {
      JsonElement s = this.get(key);
      if (s instanceof JsonNull) {
         this.properties.addProperty(key, def);
         this.writeProperties = true;
         return def;
      } else {
         return s.getAsString();
      }
   }

   public JsonElement get(String key, JsonElement def) {
      JsonElement s = this.get(key);
      if (s instanceof JsonNull) {
         this.properties.add(key, def);
         this.writeProperties = true;
         return def;
      } else {
         return s;
      }
   }

   public boolean get(String key, boolean def) {
      return this.get(key, new JsonPrimitive(def)).getAsBoolean();
   }

   public int get(String key, int def) {
      return this.get(key, new JsonPrimitive(def)).getAsInt();
   }

   public double get(String key, double def) {
      return this.get(key, new JsonPrimitive(def)).getAsDouble();
   }

   public void set(String key, JsonElement json) {
      this.properties.add(key, json);
      this.writeProperties = true;
   }

   public void save() {
      try (BufferedWriter writer = Files.newBufferedWriter(this.path)) {
         GSON.toJson(this.properties, writer);
      } catch (Exception var6) {
         ConsoleJS.STARTUP.error("Error saving properties file %s! Settings will not be saved!".formatted(this.path), var6);
      }
   }

   @Override
   public String toString() {
      return this.name;
   }
}
