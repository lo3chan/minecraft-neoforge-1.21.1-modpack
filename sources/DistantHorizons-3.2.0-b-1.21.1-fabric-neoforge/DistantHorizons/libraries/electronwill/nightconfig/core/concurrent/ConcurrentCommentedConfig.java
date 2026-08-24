package DistantHorizons.libraries.electronwill.nightconfig.core.concurrent;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ConcurrentCommentedConfig extends CommentedConfig, ConcurrentConfig {
   <R> R bulkCommentedRead(Function<? super UnmodifiableCommentedConfig, R> function);

   default void bulkCommentedRead(Consumer<? super UnmodifiableCommentedConfig> action) {
      this.bulkCommentedRead(config -> {
         action.accept(config);
         return null;
      });
   }

   <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> function);

   default void bulkCommentedUpdate(Consumer<? super CommentedConfig> action) {
      this.bulkCommentedUpdate(config -> {
         action.accept(config);
         return null;
      });
   }

   @Override
   default <R> R bulkRead(Function<? super UnmodifiableConfig, R> action) {
      return this.bulkCommentedRead(action);
   }

   @Override
   default <R> R bulkUpdate(Function<? super Config, R> action) {
      return this.bulkCommentedUpdate(action);
   }

   ConcurrentCommentedConfig createSubConfig();
}
