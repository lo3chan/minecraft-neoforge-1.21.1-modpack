package mezz.jei.api.runtime;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IJeiFeatures {
   void disableJeiGui();

   boolean isJeiGuiEnabled();

   void disableInventoryEffectRendererGuiHandler();
}
