package mezz.jei.common;

import mezz.jei.api.runtime.IJeiFeatures;

public class JeiFeatures implements IJeiFeatures {
   private boolean jeiGuiEnabled = true;
   private boolean inventoryEffectRendererGuiHandlerEnabled = true;

   @Override
   public void disableJeiGui() {
      this.jeiGuiEnabled = false;
   }

   @Override
   public boolean isJeiGuiEnabled() {
      return this.jeiGuiEnabled;
   }

   @Override
   public void disableInventoryEffectRendererGuiHandler() {
      this.inventoryEffectRendererGuiHandlerEnabled = false;
   }

   public boolean getInventoryEffectRendererGuiHandlerEnabled() {
      return this.inventoryEffectRendererGuiHandlerEnabled;
   }
}
