package fuzs.puzzleslib.api.init.v4.registry;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RegistryFactory {
   RegistryFactory INSTANCE = ProxyImpl.get().getRegistryFactoryV4();

   default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey) {
      return this.create(registryKey, (String)null);
   }

   default <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey) {
      return this.createSynced(registryKey, (String)null);
   }

   default <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, @Nullable String defaultKey) {
      return this.create(registryKey, defaultKey != null ? registryKey.location().withPath(defaultKey) : null);
   }

   default <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey, @Nullable String defaultKey) {
      return this.createSynced(registryKey, defaultKey != null ? registryKey.location().withPath(defaultKey) : null);
   }

   <T> Registry<T> create(ResourceKey<Registry<T>> var1, @Nullable ResourceLocation var2);

   <T> Registry<T> createSynced(ResourceKey<Registry<T>> var1, @Nullable ResourceLocation var2);
}
