package fuzs.puzzleslib.api.core.v1.context;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface DataPackRegistriesContext {
   <T> void registerRegistry(ResourceKey<Registry<T>> var1, Codec<T> var2);

   default <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
      this.registerSyncedRegistry(registryKey, codec, codec);
   }

   <T> void registerSyncedRegistry(ResourceKey<Registry<T>> var1, Codec<T> var2, Codec<T> var3);
}
