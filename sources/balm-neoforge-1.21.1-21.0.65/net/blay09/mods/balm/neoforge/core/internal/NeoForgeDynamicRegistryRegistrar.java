package net.blay09.mods.balm.neoforge.core.internal;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.core.AbstractDynamicRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

public class NeoForgeDynamicRegistryRegistrar {
   private final List<NeoForgeDynamicRegistryRegistrar.DynamicRegistryData<?>> registries = new ArrayList<>();

   public <T> void add(ResourceKey<? extends Registry<T>> registryKey, Codec<T> codec, AbstractDynamicRegistryBuilder<T> builder) {
      this.registries.add(new NeoForgeDynamicRegistryRegistrar.DynamicRegistryData<>(registryKey, codec, builder));
   }

   @SubscribeEvent
   public void registerRegistries(NewRegistry event) {
      for (NeoForgeDynamicRegistryRegistrar.DynamicRegistryData<?> registry : this.registries) {
         registry.register(event);
      }
   }

   private record DynamicRegistryData<T>(ResourceKey<? extends Registry<T>> registryKey, Codec<T> codec, AbstractDynamicRegistryBuilder<T> builder) {
      public void register(NewRegistry event) {
         Codec<T> networkCodec = this.builder.shouldSync() ? this.builder.getNetworkCodec() : null;
         event.dataPackRegistry(this.registryKey, this.codec, networkCodec != null ? networkCodec : (this.builder.shouldSync() ? this.codec : null));
      }
   }
}
