package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ConvertedCommentedFileConfig extends AbstractConvertedCommentedConfig<CommentedFileConfig> implements CommentedFileConfig {
   public ConvertedCommentedFileConfig(CommentedFileConfig config, ConversionTable readTable, ConversionTable writeTable, Predicate<Class<?>> supportPredicate) {
      this(config, readTable::convert, writeTable::convert, supportPredicate);
   }

   public ConvertedCommentedFileConfig(
      CommentedFileConfig config, Function<Object, Object> readConversion, Function<Object, Object> writeConversion, Predicate<Class<?>> supportPredicate
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
   public <R> R bulkCommentedRead(Function<? super UnmodifiableCommentedConfig, R> action) {
      return this.config.bulkCommentedRead(action);
   }

   @Override
   public <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> action) {
      return this.config.bulkCommentedUpdate(action);
   }

   @Override
   public <R> R bulkUpdate(Function<? super Config, R> action) {
      return this.config.bulkUpdate(action);
   }

   @Override
   public ConcurrentCommentedConfig createSubConfig() {
      return this.config.createSubConfig();
   }
}
