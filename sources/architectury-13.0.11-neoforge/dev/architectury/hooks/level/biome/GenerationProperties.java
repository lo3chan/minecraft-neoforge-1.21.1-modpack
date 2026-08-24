package dev.architectury.hooks.level.biome;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface GenerationProperties {
   Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(Carving var1);

   Iterable<Holder<PlacedFeature>> getFeatures(Decoration var1);

   List<Iterable<Holder<PlacedFeature>>> getFeatures();

   public interface Mutable extends GenerationProperties {
      GenerationProperties.Mutable addFeature(Decoration var1, Holder<PlacedFeature> var2);

      @Experimental
      GenerationProperties.Mutable addFeature(Decoration var1, ResourceKey<PlacedFeature> var2);

      GenerationProperties.Mutable addCarver(Carving var1, Holder<ConfiguredWorldCarver<?>> var2);

      @Experimental
      GenerationProperties.Mutable addCarver(Carving var1, ResourceKey<ConfiguredWorldCarver<?>> var2);

      GenerationProperties.Mutable removeFeature(Decoration var1, ResourceKey<PlacedFeature> var2);

      GenerationProperties.Mutable removeCarver(Carving var1, ResourceKey<ConfiguredWorldCarver<?>> var2);
   }
}
