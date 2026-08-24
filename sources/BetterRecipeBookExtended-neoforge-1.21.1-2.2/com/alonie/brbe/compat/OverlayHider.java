package com.alonie.brbe.compat;

import com.alonie.brbe.api.hud.HudHider;
import java.util.ArrayList;
import java.util.List;

public final class OverlayHider {
   private static final List<HudHider> HIDERS = new ArrayList<>();
   private static boolean currentlyHidden;

   private OverlayHider() {
   }

   public static void register(HudHider hider) {
      HIDERS.add(hider);
   }

   public static boolean isApplicable() {
      for (HudHider hider : HIDERS) {
         if (hider.isAvailable()) {
            return true;
         }
      }

      return false;
   }

   public static void setOverlaysHidden(boolean hide) {
      if (hide && !currentlyHidden) {
         currentlyHidden = true;
         HIDERS.forEach(HudHider::saveState);
         HIDERS.forEach(HudHider::ensureHidden);
      } else if (!hide && currentlyHidden) {
         currentlyHidden = false;
         HIDERS.forEach(HudHider::restoreState);
      }
   }

   public static void enforceHidden() {
      HIDERS.forEach(HudHider::ensureHidden);
   }

   @Deprecated
   public static void ensureJeiOverlayHidden() {
      enforceHidden();
   }

   public static void reset() {
      currentlyHidden = false;
      HIDERS.forEach(HudHider::reset);
   }
}
