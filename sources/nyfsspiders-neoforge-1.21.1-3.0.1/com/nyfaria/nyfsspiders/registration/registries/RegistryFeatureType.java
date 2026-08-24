package com.nyfaria.nyfsspiders.registration.registries;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.resources.ResourceLocation;

public final class RegistryFeatureType<X> {
   private static final ConcurrentMap<ResourceLocation, RegistryFeatureType<?>> VALUES = new MapMaker().weakValues().makeMap();
   public static final RegistryFeatureType<Void> SYNCED = getNoArgs(ResourceLocation.withDefaultNamespace("synced"));
   public static final RegistryFeatureType<ResourceLocation> DEFAULTED = get(ResourceLocation.withDefaultNamespace("defaulted"), ResourceLocation.class);
   private final ResourceLocation id;
   private final Class<X> argumentType;

   private RegistryFeatureType(ResourceLocation id, Class<X> argumentType) {
      this.id = id;
      this.argumentType = argumentType;
   }

   public static <X> RegistryFeatureType<X> get(ResourceLocation id, Class<X> argumentType) {
      return (RegistryFeatureType<X>)VALUES.computeIfAbsent(id, resourceLocation -> new RegistryFeatureType<>(resourceLocation, argumentType));
   }

   public static RegistryFeatureType<Void> getNoArgs(ResourceLocation id) {
      return get(id, Void.class);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Class<X> getArgumentType() {
      return this.argumentType;
   }
}
