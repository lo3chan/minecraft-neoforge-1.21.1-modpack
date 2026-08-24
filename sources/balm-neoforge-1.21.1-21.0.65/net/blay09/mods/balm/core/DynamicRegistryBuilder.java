package net.blay09.mods.balm.core;

import com.mojang.serialization.Codec;

public interface DynamicRegistryBuilder<T> {
   DynamicRegistryBuilder<T> sync();

   DynamicRegistryBuilder<T> sync(Codec<T> var1);

   DynamicRegistryBuilder<T> skipSyncWhenEmpty();
}
