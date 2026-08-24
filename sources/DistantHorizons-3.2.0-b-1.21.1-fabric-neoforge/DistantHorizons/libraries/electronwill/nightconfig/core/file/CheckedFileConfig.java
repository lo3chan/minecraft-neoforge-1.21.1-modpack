package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConfigWrapper;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingMap;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingSet;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class CheckedFileConfig extends ConfigWrapper<FileConfig> implements FileConfig {
   CheckedFileConfig(FileConfig config) {
      super(config);
   }

   @Override
   public Path getNioPath() {
      return this.config.getNioPath();
   }

   @Override
   public File getFile() {
      return this.config.getFile();
   }

   @Override
   public void save() {
      this.config.save();
   }

   @Override
   public void load() {
      this.config.load();
   }

   @Override
   public void close() {
      this.config.close();
   }

   @Override
   public <R> R bulkRead(Function<? super UnmodifiableConfig, R> action) {
      return this.config.bulkRead(action);
   }

   @Override
   public <R> R bulkUpdate(Function<? super Config, R> action) {
      return this.config.bulkUpdate(action);
   }

   @Override
   public FileConfig checked() {
      return this;
   }

   @Override
   public <T> T set(List<String> path, Object value) {
      return super.set(path, this.checkedValue(value));
   }

   @Override
   public boolean add(List<String> path, Object value) {
      return super.add(path, this.checkedValue(value));
   }

   @Override
   public Map<String, Object> valueMap() {
      return new TransformingMap<>(super.valueMap(), v -> (Object)v, this::checkedValue, o -> o);
   }

   @Override
   public Set<? extends Config.Entry> entrySet() {
      return new TransformingSet<>(super.entrySet(), v -> (Config.Entry)v, this::checkedValue, o -> o);
   }

   @Override
   public String toString() {
      return "checked of " + this.config;
   }

   @Override
   public ConcurrentConfig createSubConfig() {
      return this.config.createSubConfig();
   }

   private void checkValue(Object value) {
      ConfigFormat<?> format = this.configFormat();
      if (value != null && !format.supportsType(value.getClass())) {
         throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getTypeName());
      } else if (value == null && !format.supportsType(null)) {
         throw new IllegalArgumentException("Null values aren't supported by this configuration.");
      } else {
         if (value instanceof Config) {
            ((Config)value).valueMap().forEach((k, v) -> this.checkValue(v));
         }
      }
   }

   private <T> T checkedValue(T value) {
      this.checkValue(value);
      return value;
   }
}
