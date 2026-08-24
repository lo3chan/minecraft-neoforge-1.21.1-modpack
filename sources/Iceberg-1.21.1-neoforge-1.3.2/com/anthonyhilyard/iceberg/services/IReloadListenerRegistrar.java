package com.anthonyhilyard.iceberg.services;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface IReloadListenerRegistrar {
   void registerListener(PreparableReloadListener var1, ResourceLocation var2);

   void registerListener(Supplier<PreparableReloadListener> var1, ResourceLocation var2);
}
