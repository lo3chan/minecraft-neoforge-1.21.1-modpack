package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.file.FileConfig;
import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConvertedFileConfig extends AbstractConvertedConfig<FileConfig> implements FileConfig {
   public ConvertedFileConfig(FileConfig config, ConversionTable readTable, ConversionTable writeTable, Predicate<Class<?>> supportPredicate) {
      this(config, readTable::convert, writeTable::convert, supportPredicate);
   }

   public ConvertedFileConfig(
      FileConfig config, Function<Object, Object> readConversion, Function<Object, Object> writeConversion, Predicate<Class<?>> supportPredicate
   ) {
      super(config, readConversion, writeConversion, supportPredicate);
   }

   @Override
   public File getFile() {
      return this.config.getFile();
   }

   @Override
   public java.nio.file.Path getNioPath() {
      return this.config.getNioPath();
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
   public ConcurrentConfig createSubConfig() {
      return this.config.createSubConfig();
   }
}
