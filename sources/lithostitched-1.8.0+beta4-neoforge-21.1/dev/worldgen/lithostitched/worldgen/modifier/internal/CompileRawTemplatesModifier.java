package dev.worldgen.lithostitched.worldgen.modifier.internal;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.duck.StructurePoolAccess;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record CompileRawTemplatesModifier() implements WorldgenModifier {
   public static final MapCodec<CompileRawTemplatesModifier> CODEC = MapCodec.unit(CompileRawTemplatesModifier::new);

   @Override
   public Optional<LoadPredicate> predicate() {
      return Optional.empty();
   }

   @Override
   public void apply(RegistryAccess registries) {
      for (StructureTemplatePool pool : Lithostitched.registry(registries, Registries.TEMPLATE_POOL).stream().toList()) {
         ((StructurePoolAccess)pool).compileRawTemplates();
      }
   }

   @Override
   public int priority() {
      return 2147483647;
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
