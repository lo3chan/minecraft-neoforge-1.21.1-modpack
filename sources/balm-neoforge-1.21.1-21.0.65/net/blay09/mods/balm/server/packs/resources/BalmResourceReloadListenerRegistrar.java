package net.blay09.mods.balm.server.packs.resources;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

public interface BalmResourceReloadListenerRegistrar {
   default void register(String name, PreparableReloadListener listener) {
      this.register(name, (Function<Provider, PreparableReloadListener>)(registries -> listener));
   }

   void register(String var1, Function<Provider, PreparableReloadListener> var2);

   void register(String var1, Consumer<ResourceManager> var2);
}
