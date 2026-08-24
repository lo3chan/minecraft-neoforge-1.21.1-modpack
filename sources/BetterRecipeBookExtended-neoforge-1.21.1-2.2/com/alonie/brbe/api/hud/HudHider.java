package com.alonie.brbe.api.hud;

public interface HudHider {
   void saveState();

   void ensureHidden();

   void restoreState();

   default void reset() {
   }

   default boolean isAvailable() {
      return true;
   }
}
