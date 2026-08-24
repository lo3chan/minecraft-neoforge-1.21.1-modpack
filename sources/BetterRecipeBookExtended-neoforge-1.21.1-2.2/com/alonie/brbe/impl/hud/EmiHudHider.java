package com.alonie.brbe.impl.hud;

import com.alonie.brbe.api.hud.HudHider;

public final class EmiHudHider implements HudHider {
   private static Class<?> emiConfigClass;
   private static boolean classCacheResolved;
   private boolean hidden;
   private boolean saved;
   private boolean savedEnabled;

   @Override
   public void saveState() {
      if (!this.saved) {
         this.saved = true;
         Class<?> configClass = getEmiConfigClass();
         if (configClass != null) {
            try {
               this.savedEnabled = configClass.getField("enabled").getBoolean(null);
            } catch (Exception var3) {
               this.savedEnabled = true;
            }
         }
      }
   }

   @Override
   public void ensureHidden() {
      if (isEmiLoaded() && !this.hidden) {
         Class<?> configClass = getEmiConfigClass();
         if (configClass != null) {
            try {
               configClass.getField("enabled").setBoolean(null, false);
               this.hidden = true;
            } catch (ReflectiveOperationException var3) {
            }
         }
      }
   }

   @Override
   public void restoreState() {
      if (this.saved) {
         this.saved = false;
         if (isEmiLoaded() && this.hidden) {
            this.hidden = false;
            Class<?> configClass = getEmiConfigClass();
            if (configClass != null) {
               try {
                  if (this.savedEnabled) {
                     configClass.getField("enabled").setBoolean(null, true);
                  }
               } catch (ReflectiveOperationException var3) {
               }
            }
         }
      }
   }

   @Override
   public void reset() {
      this.hidden = false;
      this.saved = false;
      this.savedEnabled = false;
   }

   @Override
   public boolean isAvailable() {
      return isEmiLoaded();
   }

   public static boolean isEmiLoaded() {
      return getEmiConfigClass() != null;
   }

   static Class<?> getEmiConfigClass() {
      if (emiConfigClass == null && !classCacheResolved) {
         classCacheResolved = true;

         try {
            emiConfigClass = Class.forName("dev.emi.emi.config.EmiConfig");
         } catch (ClassNotFoundException var1) {
            emiConfigClass = null;
         }
      }

      return emiConfigClass;
   }
}
