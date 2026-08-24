package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConfigWrapper;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractConvertedConfig<C extends Config> extends ConfigWrapper<C> {
   final Function<Object, Object> readConversion;
   final Function<Object, Object> writeConversion;
   final Predicate<Class<?>> supportPredicate;
   final ConfigFormat<?> format;

   AbstractConvertedConfig(C config, Function<Object, Object> readConversion, Function<Object, Object> writeConversion, Predicate<Class<?>> supportPredicate) {
      super(config);
      this.readConversion = readConversion;
      this.writeConversion = writeConversion;
      this.supportPredicate = supportPredicate;
      this.format = new ConvertedFormat<>(config.configFormat(), supportPredicate);
   }

   @Override
   public <T> T set(List<String> path, Object value) {
      return (T)this.readConversion.apply(this.config.set(path, this.writeConversion.apply(value)));
   }

   @Override
   public Map<String, Object> valueMap() {
      return new TransformingMap<>(this.config.valueMap(), this.readConversion, this.writeConversion, this.writeConversion);
   }

   @Override
   public <T> T getRaw(List<String> path) {
      return (T)this.readConversion.apply(this.config.getRaw(path));
   }

   @Override
   public ConfigFormat<?> configFormat() {
      return this.format;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + ':' + this.valueMap() + " (original: " + this.config + ')';
   }
}
