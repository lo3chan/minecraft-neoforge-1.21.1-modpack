package traben.tconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;
import traben.entity_texture_features.ETF;

public class TConfigHandler<T extends TConfig> {
   private final Supplier<T> newConfigSupplier;
   private final String configFileName;
   private final Class<T> configClass;
   private final String logID;
   private T CONFIG = (T)null;

   public TConfigHandler(Supplier<T> newConfigSupplier, String configFileName, String logID) {
      this.newConfigSupplier = newConfigSupplier;
      this.logID = logID;
      this.configFileName = configFileName.endsWith(".json") ? configFileName : configFileName + ".json";
      this.configClass = Objects.requireNonNull((Class<T>)newConfigSupplier.get().getClass());
      this.loadFromFile();
   }

   public T getConfig() {
      if (this.CONFIG == null) {
         this.loadFromFile();
      }

      return this.CONFIG;
   }

   public void setConfig(T CONFIG) {
      this.CONFIG = CONFIG;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TConfigHandler<?> that = (TConfigHandler<?>)o;
         return this.configClass.equals(that.configClass) && this.configFileName.equals(that.configFileName);
      } else {
         return false;
      }
   }

   public boolean configEquals(Object that) {
      if (this.CONFIG == that) {
         return true;
      } else {
         return that != null && this.CONFIG.getClass() == that.getClass() ? this.toJson().equals(this.toJson(that)) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.configFileName, this.configClass);
   }

   public void saveToFile() {
      Path configDir = ETF.getConfigDirectory();
      if (configDir != null) {
         File config = new File(configDir.toFile(), this.configFileName);
         config.getParentFile().mkdirs();

         try (FileWriter fileWriter = new FileWriter(config)) {
            fileWriter.write(this.toJson());
            fileWriter.close();
            this.loadFromFile(false);
         } catch (IOException var8) {
            TConfigLog.logError(this.logID, "Config file could not be saved: " + var8.getMessage());
         }
      }
   }

   public String toJson() {
      return this.toJson(this.CONFIG);
   }

   public String toJson(Object config) {
      return new GsonBuilder().setPrettyPrinting().create().toJson(config);
   }

   public void loadFromFile() {
      this.loadFromFile(true);
   }

   public void loadFromFile(boolean saveAfterLoad) {
      Path configDir = ETF.getConfigDirectory();
      if (configDir == null) {
         this.CONFIG = this.newConfigSupplier.get();
      } else {
         File config = new File(configDir.toFile(), this.configFileName);
         T newConfig;
         if (config.exists()) {
            try (FileReader fileReader = new FileReader(config)) {
               newConfig = this.fromJson(fileReader);
            } catch (Exception var10) {
               TConfigLog.logError(this.logID, "Config could not be loaded, using defaults");
               newConfig = this.newConfigSupplier.get();
            }
         } else {
            newConfig = this.newConfigSupplier.get();
         }

         if (newConfig == null) {
            TConfigLog.logError(this.logID, "Config was null, using defaults");
            this.CONFIG = this.newConfigSupplier.get();
         } else {
            this.CONFIG = newConfig;
         }

         if (saveAfterLoad) {
            this.saveToFile();
         }
      }
   }

   public T fromJson(String json) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return (T)gson.fromJson(json, this.configClass);
   }

   public T fromJson(FileReader json) throws JsonIOException, JsonSyntaxException {
      Gson gson = new GsonBuilder().setLenient().create();
      return (T)gson.fromJson(json, this.configClass);
   }

   public T copyOfConfig() {
      return this.fromJson(this.toJson());
   }

   public boolean doesGUI() {
      return this.getConfig().doesGUI();
   }
}
