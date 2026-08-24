package com.iafenvoy.origins.data.power;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public final class PowerRegistries {
   public static final ResourceKey<Registry<MapCodec<? extends Power>>> POWER_TYPE_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.fromNamespaceAndPath("origins", "power_type")
   );
   public static final DefaultedRegistry<MapCodec<? extends Power>> POWER_TYPE = new DefaultedMappedRegistry("empty", POWER_TYPE_KEY, Lifecycle.stable(), false);
   public static final ResourceKey<Registry<Power>> POWER_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("origins", "power"));

   @SubscribeEvent
   public static void newRegistries(NewRegistryEvent event) {
      event.register(POWER_TYPE);
   }
}
