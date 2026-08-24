package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import net.minecraft.client.KeyMapping;

@FunctionalInterface
public interface KeyMappingsContext {
   default void registerKeyMapping(KeyMapping keyMapping) {
      this.registerKeyMapping(keyMapping, KeyActivationContext.UNIVERSAL);
   }

   default void registerKeyMapping(KeyMapping keyMapping, KeyActivationContext activationContext) {
      this.registerKeyMapping(keyMapping, KeyActivationHandler.direct(activationContext));
   }

   void registerKeyMapping(KeyMapping var1, KeyActivationHandler var2);
}
