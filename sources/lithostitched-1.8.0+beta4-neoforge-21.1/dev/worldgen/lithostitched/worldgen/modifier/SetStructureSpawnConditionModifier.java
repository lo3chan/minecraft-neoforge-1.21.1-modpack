package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

public record SetStructureSpawnConditionModifier(
   Optional<LoadPredicate> predicate, int priority, HolderSet<Structure> structures, PlacementCondition spawnCondition, boolean append
) implements WorldgenModifier {
   public static final MapCodec<SetStructureSpawnConditionModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(SetStructureSpawnConditionModifier::priority),
            LithostitchedCodecs.registrySet(Registries.STRUCTURE, "structures").forGetter(SetStructureSpawnConditionModifier::structures),
            PlacementCondition.CODEC.fieldOf("spawn_condition").forGetter(SetStructureSpawnConditionModifier::spawnCondition),
            Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetStructureSpawnConditionModifier::append)
         )
         .apply(instance, SetStructureSpawnConditionModifier::new)
   );

   @Override
   public void apply(RegistryAccess registries) {
      this.structures.forEach(structure -> this.applyModifier(registries, structure));
   }

   private void applyModifier(RegistryAccess registries, Holder<Structure> structure) {
      if (structure.value() instanceof DelegatingStructure delegating) {
         delegating.config().setSpawnCondition(this.spawnCondition, this.append);
      } else if (structure instanceof Reference<Structure> reference) {
         Structure delegating = new DelegatingStructure(new DelegatingConfig(Holder.direct((Structure)structure.value()), Optional.of(this.spawnCondition)));
         ((HolderReferenceAccessor)structure).setValue(delegating);
         ((MappedRegistryAccessor)Lithostitched.registry(registries, Registries.STRUCTURE)).getByValue().put(delegating, reference);
      }
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
