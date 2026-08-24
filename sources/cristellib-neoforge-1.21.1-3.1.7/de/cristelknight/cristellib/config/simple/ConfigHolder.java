package de.cristelknight.cristellib.config.simple;

import de.cristelknight.cristellib.PlatformHelper;
import de.cristelknight.cristellib.config.ConfigManager;
import de.cristelknight.cristellib.config.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ConfigHolder<T> {
   private final ConfigSettings<T> spec;
   private T instance;

   public ConfigHolder(ConfigSettings<T> spec) {
      this.spec = spec;
   }

   public T getInstance() {
      if (this.instance == null) {
         this.instance = this.readOrCreate();
      }

      return this.instance;
   }

   public void update(T newData) {
      this.instance = newData;
   }

   public void updateAndSave(T newData) {
      this.instance = newData;
      this.save();
   }

   public void save() {
      this.write(this.instance);
   }

   public ConfigSettings<T> getSettings() {
      return this.spec;
   }

   private T readOrCreate() {
      Path path = this.getPath();
      if (!Files.exists(path)) {
         this.write(this.spec.getDefault());
      }

      return ConfigManager.readFromJanksonPathWithFix(path, this.spec.getCodec(), this::write);
   }

   private void write(T data) {
      FileWriter.writeToFile(
         this.getPath(),
         this.spec.getCodec(),
         getSafeComments(this.spec.getComments()),
         data,
         ConfigManager.createHeader(this.spec.getHeader()),
         this.spec.isSorted()
      );
   }

   private Path getPath() {
      return PlatformHelper.getConfigDirectory().resolve(this.spec.getSubPath() + ".json5");
   }

   public static HashMap<String, String> getSafeComments(HashMap<String, String> comments) {
      return comments == null ? new HashMap<>() : comments;
   }
}
