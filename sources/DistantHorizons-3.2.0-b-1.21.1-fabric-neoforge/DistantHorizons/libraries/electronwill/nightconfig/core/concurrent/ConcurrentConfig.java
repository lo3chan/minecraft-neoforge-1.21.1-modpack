package DistantHorizons.libraries.electronwill.nightconfig.core.concurrent;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ConcurrentConfig extends Config {
   <R> R bulkRead(Function<? super UnmodifiableConfig, R> function);

   default void bulkRead(Consumer<? super UnmodifiableConfig> action) {
      this.bulkRead(config -> {
         action.accept(config);
         return null;
      });
   }

   <R> R bulkUpdate(Function<? super Config, R> function);

   default void bulkUpdate(Consumer<? super Config> action) {
      this.bulkUpdate(config -> {
         action.accept(config);
         return null;
      });
   }

   ConcurrentConfig createSubConfig();
}
