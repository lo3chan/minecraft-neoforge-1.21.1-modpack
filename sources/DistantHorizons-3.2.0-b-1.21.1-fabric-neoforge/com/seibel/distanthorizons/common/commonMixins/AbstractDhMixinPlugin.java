package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;

public abstract class AbstractDhMixinPlugin {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public boolean shouldApplyDhMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.endsWith("MixinImmersivePortalsRenderStates")) {
         boolean immersivePortalsPresent = false;

         try {
            Thread.currentThread().getContextClassLoader().loadClass("qouteall.imm_ptl.core.render.context_management.RenderStates");
            immersivePortalsPresent = true;
         } catch (ClassNotFoundException var6) {
         }

         if (!immersivePortalsPresent) {
            try {
               Thread.currentThread().getContextClassLoader().loadClass("com.qouteall.immersive_portals.render.context_management.RenderStates");
               immersivePortalsPresent = true;
            } catch (ClassNotFoundException var5) {
            }
         }

         if (!immersivePortalsPresent) {
            LOGGER.info("Immersive Portals not present, skipping DH compatibility mixin.");
         }

         return immersivePortalsPresent;
      } else {
         return true;
      }
   }
}
