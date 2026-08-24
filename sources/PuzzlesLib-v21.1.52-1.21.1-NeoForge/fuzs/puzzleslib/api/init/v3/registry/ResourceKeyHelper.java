package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class ResourceKeyHelper {
   private ResourceKeyHelper() {
   }

   public static MutableComponent getComponent(ResourceKey<?> resourceKey) {
      return Component.translatable(getTranslationKey(resourceKey));
   }

   public static MutableComponent getComponent(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
      return Component.translatable(getTranslationKey(registryKey, resourceLocation));
   }

   public static String getTranslationKey(ResourceKey<?> resourceKey) {
      return getTranslationKey(resourceKey.registryKey(), resourceKey.location());
   }

   public static String getTranslationKey(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
      return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), resourceLocation);
   }

   public static ResourceLocation getResourceLocation(ResourceKey<?> resourceKey) {
      return getResourceLocation(resourceKey.registryKey(), resourceKey.location());
   }

   public static ResourceLocation getResourceLocation(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
      return resourceLocation.withPrefix(Registries.elementsDirPath(registryKey) + ".");
   }
}
