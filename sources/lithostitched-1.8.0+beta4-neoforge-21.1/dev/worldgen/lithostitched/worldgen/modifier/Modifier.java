package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;

@Deprecated(
   forRemoval = true
)
public interface Modifier extends WorldgenModifier {
   MapCodec<Integer> PRIORITY_DEFAULT = WorldgenModifier.PRIORITY_DEFAULT_CODEC;
   MapCodec<Integer> PRIORITY_REMOVE = WorldgenModifier.PRIORITY_REMOVE_CODEC;

   @Override
   default Optional<LoadPredicate> predicate() {
      return Optional.empty();
   }

   @Override
   default void apply(RegistryAccess registries) {
      this.applyModifier(registries);
   }

   @Deprecated
   default void applyModifier(RegistryAccess registryAccess) {
      this.applyModifier();
   }

   @Deprecated
   void applyModifier();
}
