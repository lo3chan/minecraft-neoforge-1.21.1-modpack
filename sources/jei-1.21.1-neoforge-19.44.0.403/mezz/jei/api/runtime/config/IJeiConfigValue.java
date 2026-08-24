package mezz.jei.api.runtime.config;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;

public interface IJeiConfigValue<T> {
   String getName();

   @Deprecated(
      since = "19.21.0",
      forRemoval = true
   )
   String getDescription();

   Component getLocalizedName();

   Component getLocalizedDescription();

   T getValue();

   T getDefaultValue();

   boolean set(T var1);

   default void addListener(Consumer<T> listener) {
   }

   IJeiConfigValueSerializer<T> getSerializer();
}
