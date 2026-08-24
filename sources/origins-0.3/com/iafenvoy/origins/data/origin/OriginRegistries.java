package com.iafenvoy.origins.data.origin;

import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class OriginRegistries {
   public static final ResourceKey<Registry<Origin>> ORIGIN_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("origins", "origin"));

   public static Stream<Reference<Origin>> streamAvailableOrigins(RegistryAccess access) {
      return access.registryOrThrow(ORIGIN_KEY).holders().filter(x -> !((Origin)x.value()).unchoosable());
   }
}
