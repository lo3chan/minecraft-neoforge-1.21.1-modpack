package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

public interface CommentedFileConfig extends ConcurrentCommentedConfig, FileConfig {
   default CommentedFileConfig checked() {
      return new CheckedCommentedFileConfig(this);
   }

   @Override
   <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> function);

   @Override
   default void bulkCommentedUpdate(Consumer<? super CommentedConfig> action) {
      this.bulkCommentedUpdate(config -> {
         action.accept(config);
         return null;
      });
   }

   @Override
   default <R> R bulkUpdate(Function<? super Config, R> action) {
      return this.bulkCommentedUpdate(action);
   }

   static CommentedFileConfig of(File file) {
      return of(file.toPath());
   }

   static CommentedFileConfig of(File file, ConfigFormat<? extends CommentedConfig> format) {
      return of(file.toPath(), format);
   }

   static CommentedFileConfig of(Path file) {
      ConfigFormat format = FormatDetector.detect(file);
      if (format != null && format.supportsComments()) {
         return of(file, format);
      } else {
         throw new NoFormatFoundException("No suitable format for " + file.getFileName());
      }
   }

   static CommentedFileConfig of(Path file, ConfigFormat<? extends CommentedConfig> format) {
      return builder(file, format).build();
   }

   static CommentedFileConfig of(String filePath) {
      return of(Paths.get(filePath));
   }

   static CommentedFileConfig of(String filePath, ConfigFormat<? extends CommentedConfig> format) {
      return of(Paths.get(filePath), format);
   }

   @Deprecated
   static CommentedFileConfig ofConcurrent(File file) {
      return ofConcurrent(file.toPath());
   }

   @Deprecated
   static CommentedFileConfig ofConcurrent(File file, ConfigFormat<? extends CommentedConfig> format) {
      return ofConcurrent(file.toPath(), format);
   }

   @Deprecated
   static CommentedFileConfig ofConcurrent(Path file) {
      return builder(file).concurrent().build();
   }

   @Deprecated
   static CommentedFileConfig ofConcurrent(Path file, ConfigFormat<? extends CommentedConfig> format) {
      return builder(file, format).concurrent().build();
   }

   @Deprecated
   static CommentedFileConfig ofConcurrent(String filePath, ConfigFormat<? extends CommentedConfig> format) {
      return ofConcurrent(Paths.get(filePath), format);
   }

   @Deprecated
   static CommentedFileConfig ofConcurrent(String filePath) {
      return ofConcurrent(Paths.get(filePath));
   }

   static CommentedFileConfigBuilder builder(File file, ConfigFormat<? extends CommentedConfig> format) {
      return builder(file.toPath(), format);
   }

   static CommentedFileConfigBuilder builder(File file) {
      return builder(file.toPath());
   }

   static CommentedFileConfigBuilder builder(Path file, ConfigFormat<? extends CommentedConfig> format) {
      return new CommentedFileConfigBuilder(file, format);
   }

   static CommentedFileConfigBuilder builder(Path file) {
      ConfigFormat format = FormatDetector.detect(file);
      if (format == null) {
         throw new NoFormatFoundException("No suitable format for " + file.getFileName());
      } else if (!format.supportsComments()) {
         throw new NoFormatFoundException("The available format doesn't support comments for " + file.getFileName());
      } else {
         return builder(file, format);
      }
   }

   static CommentedFileConfigBuilder builder(String filePath) {
      return builder(Paths.get(filePath));
   }

   static CommentedFileConfigBuilder builder(String filePath, ConfigFormat<? extends CommentedConfig> format) {
      return builder(Paths.get(filePath), format);
   }
}
