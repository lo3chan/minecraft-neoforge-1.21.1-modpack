package dev.corgitaco.enhancedcelestials2core.api;

import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEventChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;

public final class EnhancedCelestialsEvents {
   private static final List<LunarEventChangeListener> LUNAR_EVENT_CHANGE_LISTENERS = new CopyOnWriteArrayList<>();

   private EnhancedCelestialsEvents() {
   }

   public static void registerLunarEventChanged(LunarEventChangeListener listener) {
      LUNAR_EVENT_CHANGE_LISTENERS.add(listener);
   }

   public static void fireLunarEventChanged(ServerLevel level, Holder<LunarEvent> previousEvent, Holder<LunarEvent> newEvent) {
      for (LunarEventChangeListener listener : LUNAR_EVENT_CHANGE_LISTENERS) {
         listener.onLunarEventChanged(level, previousEvent, newEvent);
      }
   }
}
