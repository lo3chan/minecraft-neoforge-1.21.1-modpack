package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConcurrentCommentedConfigWrapper;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingMap;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingSet;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class CheckedCommentedFileConfig extends ConcurrentCommentedConfigWrapper<CommentedFileConfig> implements CommentedFileConfig {
   CheckedCommentedFileConfig(CommentedFileConfig config) {
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
   public <R> R bulkCommentedRead(Function<? super UnmodifiableCommentedConfig, R> action) {
      return this.config.bulkCommentedRead(action);
   }

   @Override
   public <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> action) {
      return this.config.bulkCommentedUpdate(action);
   }

   @Override
   public CommentedFileConfig checked() {
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
   public Set<? extends CommentedConfig.Entry> entrySet() {
      return new TransformingSet<>(super.entrySet(), v -> (CommentedConfig.Entry)v, this::checkedValue, o -> o);
   }

   @Override
   public String toString() {
      return "checked of " + this.config;
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
