package net.blay09.mods.balm.neoforge.world;

import net.blay09.mods.balm.api.world.BiomePredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

class BiomeModification {
   private final BiomePredicate biomePredicate;
   private final Decoration step;
   private final ResourceKey<PlacedFeature> placedFeatureKey;

   BiomeModification(BiomePredicate biomePredicate, Decoration step, ResourceKey<PlacedFeature> placedFeatureKey) {
      this.biomePredicate = biomePredicate;
      this.step = step;
      this.placedFeatureKey = placedFeatureKey;
   }

   public BiomePredicate getBiomePredicate() {
      return this.biomePredicate;
   }

   public Decoration getStep() {
      return this.step;
   }

   public ResourceKey<PlacedFeature> getConfiguredFeatureKey() {
      return this.placedFeatureKey;
   }
}
