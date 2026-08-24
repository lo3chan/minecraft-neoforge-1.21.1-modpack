package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentConfig;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

public interface FileConfig extends ConcurrentConfig, AutoCloseable {
   File getFile();

   Path getNioPath();

   void save();

   void load();

   @Override
   void close();

   default FileConfig checked() {
      return new CheckedFileConfig(this);
   }

   @Override
   <R> R bulkUpdate(Function<? super Config, R> function);

   @Override
   default void bulkUpdate(Consumer<? super Config> action) {
      this.bulkUpdate(config -> {
         action.accept(config);
         return null;
      });
   }

   static FileConfig of(File file) {
      return of(file.toPath());
   }

   static FileConfig of(File file, ConfigFormat<? extends Config> format) {
      return of(file.toPath(), format);
   }

   static FileConfig of(Path file) {
      ConfigFormat<?> format = FormatDetector.detect(file);
      if (format == null) {
         throw new NoFormatFoundException("No suitable format for " + file.getFileName());
      } else {
         return of(file, (ConfigFormat<? extends Config>)format);
      }
   }

   static FileConfig of(Path file, ConfigFormat<? extends Config> format) {
      return builder(file, format).build();
   }

   static FileConfig of(String filePath) {
      return of(Paths.get(filePath));
   }

   static FileConfig of(String filePath, ConfigFormat<?> format) {
      return of(Paths.get(filePath), (ConfigFormat<? extends Config>)format);
   }

   @Deprecated
   static FileConfig ofConcurrent(File file) {
      return ofConcurrent(file.toPath());
   }

   @Deprecated
   static FileConfig ofConcurrent(File file, ConfigFormat<?> format) {
      return ofConcurrent(file.toPath(), format);
   }

   @Deprecated
   static FileConfig ofConcurrent(Path file) {
      return builder(file).concurrent().build();
   }

   @Deprecated
   static FileConfig ofConcurrent(Path file, ConfigFormat<?> format) {
      return builder(file, format).concurrent().build();
   }

   @Deprecated
   static FileConfig ofConcurrent(String filePath) {
      return ofConcurrent(Paths.get(filePath));
   }

   @Deprecated
   static FileConfig ofConcurrent(String filePath, ConfigFormat<?> format) {
      return ofConcurrent(Paths.get(filePath), format);
   }

   static FileConfigBuilder builder(File file) {
      return builder(file.toPath());
   }

   static FileConfigBuilder builder(File file, ConfigFormat<?> format) {
      return builder(file.toPath(), format);
   }

   static FileConfigBuilder builder(Path file) {
      ConfigFormat<?> format = FormatDetector.detect(file);
      if (format == null) {
         throw new NoFormatFoundException("No suitable format for " + file.getFileName());
      } else {
         return builder(file, format);
      }
   }

   static FileConfigBuilder builder(Path file, ConfigFormat<?> format) {
      return new FileConfigBuilder(file, (ConfigFormat<? extends Config>)format);
   }

   static FileConfigBuilder builder(String filePath) {
      return builder(Paths.get(filePath));
   }

   static FileConfigBuilder builder(String filePath, ConfigFormat<?> format) {
      return builder(Paths.get(filePath), format);
   }
}
