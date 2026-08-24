package DistantHorizons.libraries.electronwill.nightconfig.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ConfigCopier {
   private final Function<Object, Object> plainValueCopier;
   private final BiFunction<Collection<?>, CommentedConfig, Collection<Object>> collectionCopier;

   public ConfigCopier(Function<Object, Object> plainValueCopier, BiFunction<Collection<?>, CommentedConfig, Collection<Object>> collectionCopier) {
      this.plainValueCopier = plainValueCopier;
      this.collectionCopier = collectionCopier;
   }

   public void deepCopy(UnmodifiableConfig config, Supplier<Config> copyCreator) {
   }

   private void deepCopyTo(UnmodifiableCommentedConfig from, CommentedConfig to) {
      for (UnmodifiableCommentedConfig.Entry entry : from.entrySet()) {
         String key = entry.getKey();
         List<String> configKey = Collections.singletonList(key);
         Object value = entry.getValue();
         Object copiedValue;
         if (value instanceof UnmodifiableConfig) {
            CommentedConfig toSub = to.createSubConfig();
            this.deepCopyTo(UnmodifiableCommentedConfig.fake((UnmodifiableConfig)value), toSub);
            copiedValue = toSub;
         } else if (value instanceof Collection) {
            copiedValue = this.collectionCopier.apply((Collection<?>)value, to);
         } else {
            copiedValue = this.plainValueCopier.apply(value);
         }

         to.set(configKey, copiedValue);
         String comment = entry.getComment();
         if (comment != null) {
            to.setComment(configKey, comment);
         }
      }
   }

   private static final class Builder {
   }
}
