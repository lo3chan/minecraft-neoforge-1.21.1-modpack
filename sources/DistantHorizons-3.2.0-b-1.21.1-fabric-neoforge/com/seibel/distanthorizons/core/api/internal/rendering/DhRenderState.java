package com.seibel.distanthorizons.core.api.internal.rendering;

import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;

public class DhRenderState {
   public DhMat4f mcModelViewMatrix = null;
   public DhMat4f mcProjectionMatrix = null;
   public float partialTickTime = -1.0F;
   public IClientLevelWrapper clientLevelWrapper = null;
   public boolean vanillaFogEnabled = false;

   public String unableToRenderBecause() {
      String errorReasons = "";
      if (this.mcModelViewMatrix == null) {
         errorReasons = errorReasons + "no MVM Matrix, ";
      }

      if (this.mcProjectionMatrix == null) {
         errorReasons = errorReasons + "no Projection Matrix, ";
      }

      if (this.partialTickTime == -1.0F) {
         errorReasons = errorReasons + "no Frame Time, ";
      }

      if (this.clientLevelWrapper == null) {
         errorReasons = errorReasons + "no Level Wrapper, ";
      }

      return errorReasons;
   }

   public void canRenderOrThrow() throws IllegalStateException {
      String errorReasons = this.unableToRenderBecause();
      if (!errorReasons.isEmpty()) {
         throw new IllegalStateException(errorReasons);
      }
   }
}
