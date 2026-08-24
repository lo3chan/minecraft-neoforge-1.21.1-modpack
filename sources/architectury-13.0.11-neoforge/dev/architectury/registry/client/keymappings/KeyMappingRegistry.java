package dev.architectury.registry.client.keymappings;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import dev.architectury.registry.client.keymappings.forge.KeyMappingRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class KeyMappingRegistry {
   private KeyMappingRegistry() {
   }

   @ExpectPlatform
   @Transformed
   public static void register(KeyMapping mapping) {
      KeyMappingRegistryImpl.register(mapping);
   }
}
