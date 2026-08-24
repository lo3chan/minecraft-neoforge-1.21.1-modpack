package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ConvertedCommentedConfig extends AbstractConvertedCommentedConfig<CommentedConfig> {
   public ConvertedCommentedConfig(CommentedConfig config, ConversionTable readTable, ConversionTable writeTable, Predicate<Class<?>> supportPredicate) {
      this(config, readTable::convert, writeTable::convert, supportPredicate);
   }

   public ConvertedCommentedConfig(
      CommentedConfig config, Function<Object, Object> readConversion, Function<Object, Object> writeConversion, Predicate<Class<?>> supportPredicate
   ) {
      super(config, readConversion, writeConversion, supportPredicate);
   }
}
