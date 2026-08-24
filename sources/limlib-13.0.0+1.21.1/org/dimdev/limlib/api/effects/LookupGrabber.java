package org.dimdev.limlib.api.effects;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class LookupGrabber {
   public static <T> Optional<T> snatch(HolderLookup<T> lookup, ResourceKey<T> key) {
      Optional<Reference<T>> holderOptional = lookup.get(key);
      if (holderOptional.isPresent()) {
         Reference<T> holder = holderOptional.get();

         try {
            T held = (T)holder.value();
            return Optional.of(held);
         } catch (IllegalStateException var5) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static <T> Optional<T> snatch(RegistryLookup<T> lookup, ResourceLocation location) {
      return snatch(lookup, ResourceKey.create(lookup.key(), location));
   }

   public static <T> Optional<T> snatchFromLevel(Level level, ResourceKey<Registry<T>> key) {
      return level.registryAccess().lookup(key).flatMap(a -> snatch((RegistryLookup<? extends T>)a, level.dimension().location()));
   }
}
