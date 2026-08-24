package vectorwing.farmersdelight.common.world.modifier;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import vectorwing.farmersdelight.common.registry.ModBiomeModifiers;

public record AddFeaturesByFilterBiomeModifier(
   HolderSet<Biome> allowedBiomes,
   Optional<HolderSet<Biome>> deniedBiomes,
   Optional<Float> minimumTemperature,
   Optional<Float> maximumTemperature,
   HolderSet<PlacedFeature> features,
   Decoration step
) implements BiomeModifier {
   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD && this.allowedBiomes.contains(biome)) {
         if (this.deniedBiomes.isPresent() && this.deniedBiomes.get().contains(biome)) {
            return;
         }

         if (this.minimumTemperature.isPresent() && ((Biome)biome.value()).getBaseTemperature() < this.minimumTemperature.get()) {
            return;
         }

         if (this.maximumTemperature.isPresent() && ((Biome)biome.value()).getBaseTemperature() > this.maximumTemperature.get()) {
            return;
         }

         BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
         this.features.forEach(holder -> generationSettings.addFeature(this.step, holder));
      }
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return ModBiomeModifiers.ADD_FEATURES_BY_FILTER.get();
   }
}
