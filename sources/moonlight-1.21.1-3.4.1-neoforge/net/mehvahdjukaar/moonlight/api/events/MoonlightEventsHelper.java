package net.mehvahdjukaar.moonlight.api.events;

import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.events.platform.MoonlightEventsHelperImpl;

public class MoonlightEventsHelper {
   public static <T extends SimpleEvent> void addListener(Consumer<T> var0, Class<T> var1) {
      MoonlightEventsHelperImpl.addListener(var0, var1);
   }

   public static <T extends SimpleEvent> void postEvent(T var0, Class<T> var1) {
      MoonlightEventsHelperImpl.postEvent(var0, var1);
   }
}
