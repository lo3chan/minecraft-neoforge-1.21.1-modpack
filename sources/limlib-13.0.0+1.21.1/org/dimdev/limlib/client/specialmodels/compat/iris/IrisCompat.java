package org.dimdev.limlib.client.specialmodels.compat.iris;

import org.dimdev.limlib.impl.Limlib;

public final class IrisCompat {
   public static boolean shouldDisableSpecialModelRenderTypes() {
      if (Limlib.isModLoaded("iris")) {
         return false;
      } else {
         try {
            return IrisApiProxy.shouldDisableSpecialModelRenderTypes();
         } catch (RuntimeException | LinkageError var1) {
            return true;
         }
      }
   }

   private IrisCompat() {
   }
}
