package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface GenerationSettingsContext {
   boolean removeFeature(Decoration var1, ResourceKey<PlacedFeature> var2);

   default boolean removeFeature(ResourceKey<PlacedFeature> featureKey) {
      boolean anyFound = false;

      for (Decoration step : Decoration.values()) {
         if (this.removeFeature(step, featureKey)) {
            anyFound = true;
         }
      }

      return anyFound;
   }

   void addFeature(Decoration var1, ResourceKey<PlacedFeature> var2);

   void addCarver(Carving var1, ResourceKey<ConfiguredWorldCarver<?>> var2);

   boolean removeCarver(Carving var1, ResourceKey<ConfiguredWorldCarver<?>> var2);

   default boolean removeCarver(ResourceKey<ConfiguredWorldCarver<?>> carverKey) {
      boolean anyFound = false;

      for (Carving step : Carving.values()) {
         if (this.removeCarver(step, carverKey)) {
            anyFound = true;
         }
      }

      return anyFound;
   }

   Iterable<Holder<PlacedFeature>> getFeatures(Decoration var1);

   Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(Carving var1);
}
