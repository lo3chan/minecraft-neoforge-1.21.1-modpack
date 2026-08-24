package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ConvertedConfig extends AbstractConvertedConfig<Config> {
   public ConvertedConfig(Config config, ConversionTable readTable, ConversionTable writeTable, Predicate<Class<?>> supportPredicate) {
      this(config, readTable::convert, writeTable::convert, supportPredicate);
   }

   public ConvertedConfig(
      Config config, Function<Object, Object> readConversion, Function<Object, Object> writeConversion, Predicate<Class<?>> supportPredicate
   ) {
      super(config, readConversion, writeConversion, supportPredicate);
   }

   @Override
   public Set<? extends Config.Entry> entrySet() {
      Function<Config.Entry, Config.Entry> readTransfo = entry -> new Config.Entry() {
         @Override
         public Object setValue(Object value) {
            return ConvertedConfig.this.readConversion.apply(entry.setValue(ConvertedConfig.this.writeConversion.apply(value)));
         }

         @Override
         public String getKey() {
            return entry.getKey();
         }

         @Override
         public <T> T getRawValue() {
            return (T)ConvertedConfig.this.readConversion.apply(entry.getRawValue());
         }
      };
      return new TransformingSet<>(this.config.entrySet(), readTransfo, o -> null, e -> e);
   }
}
