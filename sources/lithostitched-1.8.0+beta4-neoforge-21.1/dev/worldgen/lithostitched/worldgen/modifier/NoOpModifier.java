package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;

public record NoOpModifier() implements WorldgenModifier {
   public static final MapCodec<NoOpModifier> CODEC = MapCodec.unit(NoOpModifier::new);

   @Override
   public Optional<LoadPredicate> predicate() {
      return Optional.empty();
   }

   @Override
   public void apply(RegistryAccess registries) {
   }

   @Override
   public int priority() {
      return 0;
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
