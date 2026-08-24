package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ConcurrentCommentedConfigWrapper<C extends ConcurrentCommentedConfig>
   extends CommentedConfigWrapper<C>
   implements ConcurrentCommentedConfig {
   protected ConcurrentCommentedConfigWrapper(C config) {
      super(config);
   }

   @Override
   public ConcurrentCommentedConfig createSubConfig() {
      return this.config.createSubConfig();
   }

   @Override
   public void bulkRead(Consumer<? super UnmodifiableConfig> action) {
      this.config.bulkRead(action);
   }

   @Override
   public <R> R bulkRead(Function<? super UnmodifiableConfig, R> action) {
      return this.config.bulkRead(action);
   }

   @Override
   public void bulkCommentedRead(Consumer<? super UnmodifiableCommentedConfig> action) {
      this.config.bulkCommentedRead(action);
   }

   @Override
   public <R> R bulkCommentedRead(Function<? super UnmodifiableCommentedConfig, R> action) {
      return this.config.bulkCommentedRead(action);
   }

   @Override
   public void bulkUpdate(Consumer<? super Config> action) {
      this.config.bulkUpdate(action);
   }

   @Override
   public <R> R bulkUpdate(Function<? super Config, R> action) {
      return this.config.bulkUpdate(action);
   }

   @Override
   public void bulkCommentedUpdate(Consumer<? super CommentedConfig> action) {
      this.config.bulkCommentedUpdate(action);
   }

   @Override
   public <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> action) {
      return this.config.bulkCommentedUpdate(action);
   }
}
