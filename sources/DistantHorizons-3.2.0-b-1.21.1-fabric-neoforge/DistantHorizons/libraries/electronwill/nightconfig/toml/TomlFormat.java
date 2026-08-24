package DistantHorizons.libraries.electronwill.nightconfig.toml;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.file.FormatDetector;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.function.Supplier;

public final class TomlFormat implements ConfigFormat<CommentedConfig> {
   private static final TomlFormat INSTANCE = new TomlFormat();

   public static TomlFormat instance() {
      return INSTANCE;
   }

   public static CommentedConfig newConfig() {
      return INSTANCE.createConfig();
   }

   public static CommentedConfig newConfig(Supplier<Map<String, Object>> s) {
      return INSTANCE.createConfig(s);
   }

   public static CommentedConfig newConcurrentConfig() {
      return INSTANCE.createConcurrentConfig();
   }

   private TomlFormat() {
   }

   public TomlWriter createWriter() {
      return new TomlWriter();
   }

   public TomlParser createParser() {
      return new TomlParser();
   }

   public CommentedConfig createConfig(Supplier<Map<String, Object>> mapCreator) {
      return CommentedConfig.of(mapCreator, this);
   }

   @Override
   public boolean supportsComments() {
      return true;
   }

   @Override
   public boolean supportsType(Class<?> type) {
      return type != null && (ConfigFormat.super.supportsType(type) || Temporal.class.isAssignableFrom(type));
   }

   static {
      FormatDetector.registerExtension("toml", INSTANCE);
   }
}
