package com.iafenvoy.origins.data.layer;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class LayerRegistries {
   public static final ResourceKey<Registry<Layer>> LAYER_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("origins", "layer"));

   public static Stream<Holder<Layer>> streamAvailableLayers(RegistryAccess access) {
      return access.registryOrThrow(LAYER_KEY).holders().filter(x -> ((Layer)x.value()).enabled()).map(Holder.class::cast);
   }

   public static Stream<Holder<Layer>> streamAutoChooseLayers(RegistryAccess access) {
      return streamAvailableLayers(access).filter(x -> ((Layer)x.value()).autoChoose());
   }

   public static Stream<Holder<Layer>> streamRandomizableLayers(RegistryAccess access) {
      return streamAvailableLayers(access).filter(x -> ((Layer)x.value()).allowRandom());
   }
}
