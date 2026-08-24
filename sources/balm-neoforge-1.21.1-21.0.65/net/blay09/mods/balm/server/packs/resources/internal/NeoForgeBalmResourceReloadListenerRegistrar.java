package net.blay09.mods.balm.server.packs.resources.internal;

import java.util.function.Consumer;
import java.util.function.Function;
import net.blay09.mods.balm.server.packs.resources.BalmResourceReloadListenerRegistrar;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class NeoForgeBalmResourceReloadListenerRegistrar implements BalmResourceReloadListenerRegistrar {
   private final AddReloadListenerEvent event;

   public NeoForgeBalmResourceReloadListenerRegistrar(AddReloadListenerEvent event) {
      this.event = event;
   }

   @Override
   public void register(String name, Function<Provider, PreparableReloadListener> listenerFactory) {
      this.event.addListener(listenerFactory.apply(this.event.getServerResources().getRegistryLookup()));
   }

   @Override
   public void register(String name, Consumer<ResourceManager> reloadListener) {
      this.event.addListener(reloadListener::accept);
   }
}
