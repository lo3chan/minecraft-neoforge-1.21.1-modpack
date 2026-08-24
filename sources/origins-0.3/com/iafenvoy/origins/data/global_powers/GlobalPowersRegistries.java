package com.iafenvoy.origins.data.global_powers;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class GlobalPowersRegistries {
   public static final ResourceKey<Registry<GlobalPowers>> GLOBAL_POWERS_LEY = ResourceKey.createRegistryKey(
      ResourceLocation.fromNamespaceAndPath("origins", "global_powers")
   );

   public static Stream<Holder<Power>> streamPowersForType(RegistryAccess access, EntityType<?> type) {
      return access.registryOrThrow(GLOBAL_POWERS_LEY)
         .holders()
         .<GlobalPowers>map(Reference::value)
         .filter(x -> x.entityTypes().stream().anyMatch(e -> (Boolean)e.map(type::equals, type::is)))
         .map(GlobalPowers::powers)
         .map(e -> RegistryCodecs.listAll((List<Either<Holder<Power>, TagKey<Power>>>)e, access, PowerRegistries.POWER_KEY))
         .flatMap(Collection::stream);
   }
}
