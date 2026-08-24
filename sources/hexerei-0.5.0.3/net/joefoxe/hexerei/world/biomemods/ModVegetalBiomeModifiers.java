package net.joefoxe.hexerei.world.biomemods;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public record ModVegetalBiomeModifiers(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) implements BiomeModifier {
   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD && this.biomes.contains(biome)) {
         builder.getGenerationSettings().addFeature(Decoration.VEGETAL_DECORATION, this.feature);
      }
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return (MapCodec<? extends BiomeModifier>)ModBiomeModifiers.VEGETAL_MODIFIER.get();
   }
}
