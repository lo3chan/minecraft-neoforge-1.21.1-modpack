package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;

public record KeyMappingsContextNeoForgeImpl(Consumer<KeyMapping> consumer) implements KeyMappingsContext {
   @Override
   public void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
      Objects.requireNonNull(keyMapping, "key mapping is null");
      Objects.requireNonNull(activationHandler, "activation handler is null");
      this.consumer.accept(keyMapping);
      keyMapping.setKeyConflictContext((IKeyConflictContext)NeoForgeKeyMappingHelper.KEY_CONTEXTS.get(activationHandler.getActivationContext()));
      registerKeyActivationHandles(keyMapping, activationHandler);
   }

   private static void registerKeyActivationHandles(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
      if (activationHandler.gameHandler() != null) {
         NeoForge.EVENT_BUS.addListener(evt -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
               while (keyMapping.consumeClick()) {
                  activationHandler.gameHandler().accept(minecraft);
               }
            }
         });
      }

      if (activationHandler.screenHandler() != null) {
         NeoForge.EVENT_BUS.addListener(evt -> {
            if (activationHandler.screenType().isInstance(evt.getScreen()) && keyMapping.matches(evt.getKeyCode(), evt.getScanCode())) {
               activationHandler.screenHandler().accept(evt.getScreen());
               evt.setCanceled(true);
            }
         });
      }
   }
}
