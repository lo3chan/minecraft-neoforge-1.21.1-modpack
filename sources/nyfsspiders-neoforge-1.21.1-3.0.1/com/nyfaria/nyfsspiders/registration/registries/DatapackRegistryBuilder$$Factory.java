package com.nyfaria.nyfsspiders.registration.registries;

import com.nyfaria.nyfsspiders.registration.util.$InternalRegUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface DatapackRegistryBuilder$$Factory {
   DatapackRegistryBuilder$$Factory INSTANCE = $InternalRegUtils.getOneAndOnlyService(DatapackRegistryBuilder$$Factory.class);

   <T> DatapackRegistryBuilder<T> newBuilder(ResourceKey<Registry<T>> var1);
}
