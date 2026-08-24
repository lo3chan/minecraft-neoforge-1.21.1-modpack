package net.mehvahdjukaar.moonlight.api.client.gui;

import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.core.client.config.ConfigControllers;
import net.minecraft.client.gui.components.AbstractWidget;

public record ConfigControl<T>(AbstractWidget widget, Consumer<T> valueSetter) {
   public void set(Object value) {
      this.valueSetter.accept((T)value);
   }

   public static <O extends ConfigOption<?>> void register(Class<O> type, ConfigControl.Provider<O> provider) {
      ConfigControllers.register(type, provider);
   }

   @FunctionalInterface
   public interface Provider<O extends ConfigOption<?>> {
      ConfigControl<?> create(O var1, ConfigEditSession var2, Runnable var3);
   }
}
