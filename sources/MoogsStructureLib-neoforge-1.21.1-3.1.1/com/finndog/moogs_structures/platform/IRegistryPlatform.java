package com.finndog.moogs_structures.platform;

import com.finndog.moogs_structures.modinit.registry.CustomRegistryLookup;
import com.finndog.moogs_structures.modinit.registry.ResourcefulRegistry;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.tuple.Pair;

public interface IRegistryPlatform {
   <T> ResourcefulRegistry<T> create(Registry<T> var1, String var2);

   <T, K extends Registry<T>> Pair<Supplier<CustomRegistryLookup<T>>, ResourcefulRegistry<T>> createCustomRegistryInternal(
      String var1, ResourceKey<K> var2, boolean var3, boolean var4, boolean var5
   );
}
