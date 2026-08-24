package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public record AddSurfaceRuleModifier(
   Optional<LoadPredicate> predicate, int priority, List<ResourceKey<LevelStem>> levels, InjectionType injectionType, RuleSource surfaceRule
) implements WorldgenModifier {
   public static final MapCodec<AddSurfaceRuleModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(AddSurfaceRuleModifier::priority),
            ResourceKey.codec(Registries.LEVEL_STEM).listOf().fieldOf("levels").forGetter(AddSurfaceRuleModifier::levels),
            InjectionType.CODEC.fieldOf("injection_type").orElse(InjectionType.PREPEND).forGetter(AddSurfaceRuleModifier::injectionType),
            RuleSource.CODEC.fieldOf("surface_rule").forGetter(AddSurfaceRuleModifier::surfaceRule)
         )
         .apply(instance, AddSurfaceRuleModifier::new)
   );

   @Override
   public void apply(RegistryAccess registries) {
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
