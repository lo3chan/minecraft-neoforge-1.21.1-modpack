package com.iafenvoy.origins.data.power.reference;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;

public class PowerReference {
   public static final Codec<PowerReference> CODEC = WildcardCodec.INSTANCE.xmap(PowerReference::new, p -> p.id);
   private final ResourceLocation id;

   public PowerReference(ResourceLocation id) {
      this.id = id;
   }

   public Optional<PowerHolder> get(Provider provider) {
      return getHolder(provider, this.id);
   }

   public static Optional<PowerHolder> getHolder(Provider provider, ResourceLocation id) {
      return listAllPowers(provider).filter(x -> Objects.equals(x.id(), id)).findAny();
   }

   public static Optional<PowerHolder> getHolder(Provider provider, Power power) {
      return listAllPowers(provider).filter(x -> Objects.equals(x.power(), power)).findAny();
   }

   public static Stream<PowerHolder> listAllPowers(Provider provider) {
      return provider.lookupOrThrow(PowerRegistries.POWER_KEY).listElements().map(PowerHolder::new).flatMap(PowerHolder::stream);
   }
}
