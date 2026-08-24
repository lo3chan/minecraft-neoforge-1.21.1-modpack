package fuzs.puzzleslib.api.config.v3;

import java.util.function.Consumer;

public interface ConfigDataHolder<T extends ConfigCore> {
   T getConfig();

   boolean isAvailable();

   default void addCallback(Runnable callback) {
      this.addCallback(config -> callback.run());
   }

   void addCallback(Consumer<T> var1);
}
