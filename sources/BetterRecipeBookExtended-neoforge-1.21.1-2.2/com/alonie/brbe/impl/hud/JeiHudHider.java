package com.alonie.brbe.impl.hud;

import com.alonie.brbe.api.hud.HudHider;

public final class JeiHudHider implements HudHider {
   private static Class<?> jeiToggleStateClass;
   private static boolean classCacheResolved;
   private boolean saved;
   private Boolean savedOverlayEnabled;
   private Boolean savedBookmarkEnabled;
   private Boolean savedCheatEnabled;

   @Override
   public void saveState() {
      if (!this.saved) {
         Class<?> tsClass = getJeiToggleStateClass();
         if (tsClass != null) {
            Object ts = getJeiToggleState();
            if (ts != null) {
               try {
                  this.savedOverlayEnabled = (Boolean)tsClass.getMethod("isOverlayEnabled").invoke(ts);
                  this.savedBookmarkEnabled = (Boolean)tsClass.getMethod("isBookmarkOverlayEnabled").invoke(ts);
                  this.savedCheatEnabled = (Boolean)tsClass.getMethod("isCheatItemsEnabled").invoke(ts);
                  this.saved = true;
               } catch (Exception var4) {
                  this.savedOverlayEnabled = this.savedBookmarkEnabled = this.savedCheatEnabled = null;
               }
            }
         }
      }
   }

   @Override
   public void ensureHidden() {
      Class<?> tsClass = getJeiToggleStateClass();
      if (tsClass != null) {
         Object ts = getJeiToggleState();
         if (ts != null) {
            try {
               if ((Boolean)tsClass.getMethod("isOverlayEnabled").invoke(ts)) {
                  tsClass.getMethod("toggleOverlayEnabled").invoke(ts);
               }

               if ((Boolean)tsClass.getMethod("isBookmarkOverlayEnabled").invoke(ts)) {
                  tsClass.getMethod("toggleBookmarkEnabled").invoke(ts);
               }

               if ((Boolean)tsClass.getMethod("isCheatItemsEnabled").invoke(ts)) {
                  tsClass.getMethod("toggleCheatItemsEnabled").invoke(ts);
               }
            } catch (Exception var4) {
            }
         }
      }
   }

   @Override
   public void restoreState() {
      if (this.saved) {
         this.saved = false;
         if (this.savedOverlayEnabled != null || this.savedBookmarkEnabled != null || this.savedCheatEnabled != null) {
            Class<?> tsClass = getJeiToggleStateClass();
            if (tsClass != null) {
               Object ts = getJeiToggleState();
               if (ts != null) {
                  try {
                     if (this.savedOverlayEnabled != null) {
                        boolean cur = (Boolean)tsClass.getMethod("isOverlayEnabled").invoke(ts);
                        if (cur != this.savedOverlayEnabled) {
                           tsClass.getMethod("toggleOverlayEnabled").invoke(ts);
                        }
                     }

                     if (this.savedBookmarkEnabled != null) {
                        boolean cur = (Boolean)tsClass.getMethod("isBookmarkOverlayEnabled").invoke(ts);
                        if (cur != this.savedBookmarkEnabled) {
                           tsClass.getMethod("toggleBookmarkEnabled").invoke(ts);
                        }
                     }

                     if (this.savedCheatEnabled != null) {
                        boolean cur = (Boolean)tsClass.getMethod("isCheatItemsEnabled").invoke(ts);
                        if (cur != this.savedCheatEnabled) {
                           tsClass.getMethod("toggleCheatItemsEnabled").invoke(ts);
                        }
                     }
                  } catch (Exception var4) {
                  }

                  this.savedOverlayEnabled = this.savedBookmarkEnabled = this.savedCheatEnabled = null;
               }
            }
         }
      }
   }

   @Override
   public void reset() {
      this.saved = false;
      this.savedOverlayEnabled = this.savedBookmarkEnabled = this.savedCheatEnabled = null;
   }

   private static Object getJeiToggleState() {
      try {
         return Class.forName("mezz.jei.common.Internal").getMethod("getClientToggleState").invoke(null);
      } catch (Exception var1) {
         return null;
      }
   }

   public static Class<?> getJeiToggleStateClass() {
      if (jeiToggleStateClass == null) {
         resolveClassCache();
      }

      return jeiToggleStateClass;
   }

   private static synchronized void resolveClassCache() {
      if (!classCacheResolved) {
         classCacheResolved = true;

         try {
            Class.forName("mezz.jei.common.Internal");
            jeiToggleStateClass = Class.forName("mezz.jei.common.config.IClientToggleState");
         } catch (ClassNotFoundException var1) {
            jeiToggleStateClass = null;
         }
      }
   }

   @Override
   public boolean isAvailable() {
      return getJeiToggleStateClass() != null;
   }
}
