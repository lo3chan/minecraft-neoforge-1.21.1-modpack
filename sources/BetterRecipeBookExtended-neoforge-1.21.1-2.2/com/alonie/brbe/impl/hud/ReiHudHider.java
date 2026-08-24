package com.alonie.brbe.impl.hud;

import com.alonie.brbe.api.hud.HudHider;

public final class ReiHudHider implements HudHider {
   private static Class<?> reiConfigClass;
   private static boolean classCacheResolved;
   private boolean hidden;
   private boolean saved;

   @Override
   public void saveState() {
      this.saved = true;
   }

   @Override
   public void ensureHidden() {
      if (isReiLoaded() && !this.hidden) {
         try {
            Object instance = getReiConfigClass().getMethod("getInstance").invoke(null);
            getReiConfigClass().getMethod("setOverlayVisible", boolean.class).invoke(instance, false);
            this.hidden = true;
         } catch (ReflectiveOperationException var2) {
         }
      }
   }

   @Override
   public void restoreState() {
      if (this.saved) {
         this.saved = false;
         if (isReiLoaded() && this.hidden) {
            try {
               Object instance = getReiConfigClass().getMethod("getInstance").invoke(null);
               getReiConfigClass().getMethod("setOverlayVisible", boolean.class).invoke(instance, true);
            } catch (ReflectiveOperationException var2) {
            }

            this.hidden = false;
         }
      }
   }

   @Override
   public void reset() {
      this.hidden = false;
      this.saved = false;
   }

   @Override
   public boolean isAvailable() {
      return isReiLoaded();
   }

   public static boolean isReiLoaded() {
      return getReiConfigClass() != null;
   }

   static Class<?> getReiConfigClass() {
      if (reiConfigClass == null && !classCacheResolved) {
         try {
            reiConfigClass = Class.forName("me.shedaniel.rei.api.client.config.ConfigObject");
         } catch (ClassNotFoundException var1) {
         }
      }

      return reiConfigClass;
   }
}
