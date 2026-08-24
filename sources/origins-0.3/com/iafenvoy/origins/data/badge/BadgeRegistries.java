package com.iafenvoy.origins.data.badge;

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
public final class BadgeRegistries {
   public static final ResourceKey<Registry<MapCodec<? extends Badge>>> BADGE_TYPE_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.fromNamespaceAndPath("origins", "badge_type")
   );
   public static final DefaultedRegistry<MapCodec<? extends Badge>> BADGE_TYPE = new DefaultedMappedRegistry("empty", BADGE_TYPE_KEY, Lifecycle.stable(), false);
   public static final ResourceKey<Registry<Badge>> BADGE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("origins", "badge"));

   @SubscribeEvent
   public static void newRegistries(NewRegistryEvent event) {
      event.register(BADGE_TYPE);
   }
}
