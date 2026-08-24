package net.raphimc.immediatelyfast.apiimpl;

import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfastapi.BatchingAccess;

@Deprecated
public class BatchingAccessImpl implements BatchingAccess {
   private boolean warned = false;

   @Deprecated
   @Override
   public void beginHudBatching() {
      this.warn();
   }

   @Deprecated
   @Override
   public void endHudBatching() {
      this.warn();
   }

   @Deprecated
   @Override
   public boolean isHudBatching() {
      this.warn();
      return false;
   }

   @Deprecated
   @Override
   public boolean hasDataToDraw() {
      this.warn();
      return false;
   }

   @Deprecated
   @Override
   public void forceDrawBuffers() {
      this.warn();
   }

   private void warn() {
      if (!this.warned) {
         this.warned = true;
         ImmediatelyFast.LOGGER.error("A mod tried to use the ImmediatelyFast batching API, but it is no longer available in 1.21.");
         ImmediatelyFast.LOGGER
            .error(
               "Mojang added basic batching into the DrawContext class. ImmediatelyFast now uses and extends this system, so this method is no longer needed."
            );
         ImmediatelyFast.LOGGER
            .error(
               "To migrate your mod, simply remove all calls to the ImmediatelyFast batching API and make sure to use the DrawContext for your HUD rendering."
            );
         ImmediatelyFast.LOGGER.error("Here is a stack trace to help you find the offending code:", new Exception());
      }
   }
}
