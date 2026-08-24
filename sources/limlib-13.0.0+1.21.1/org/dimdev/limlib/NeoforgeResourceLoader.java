package org.dimdev.limlib;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;

public class NeoforgeResourceLoader {
   public static class Client implements ResourceManagerReloadListener {
      private final ResourceLocation id;
      private final Consumer<ResourceManager> consumer;

      public Client(ResourceLocation id, Consumer<ResourceManager> consumer) {
         this.id = id;
         this.consumer = consumer;
      }

      public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
         this.consumer.accept(resourceManager);
      }

      public String getName() {
         return this.id.toString();
      }
   }

   public static class Server extends ContextAwareReloadListener implements ResourceManagerReloadListener {
      private final ResourceLocation id;
      private final BiConsumer<Provider, ResourceManager> consumer;

      public Server(ResourceLocation id, BiConsumer<Provider, ResourceManager> consumer) {
         this.id = id;
         this.consumer = consumer;
      }

      public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
         this.consumer.accept(this.getRegistryLookup(), resourceManager);
      }

      public String getName() {
         return this.id.toString();
      }
   }
}
