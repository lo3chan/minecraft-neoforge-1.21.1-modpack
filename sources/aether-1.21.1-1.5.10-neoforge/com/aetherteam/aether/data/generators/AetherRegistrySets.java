package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.data.resources.registries.AetherBiomes;
import com.aetherteam.aether.data.resources.registries.AetherConfiguredFeatures;
import com.aetherteam.aether.data.resources.registries.AetherDamageTypes;
import com.aetherteam.aether.data.resources.registries.AetherDensityFunctions;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.data.resources.registries.AetherJukeboxSongs;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.data.resources.registries.AetherNoiseSettings;
import com.aetherteam.aether.data.resources.registries.AetherNoises;
import com.aetherteam.aether.data.resources.registries.AetherPlacedFeatures;
import com.aetherteam.aether.data.resources.registries.AetherStructureProcessorLists;
import com.aetherteam.aether.data.resources.registries.AetherStructureSets;
import com.aetherteam.aether.data.resources.registries.AetherStructures;
import com.aetherteam.aether.data.resources.registries.AetherTrimMaterials;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class AetherRegistrySets extends DatapackBuiltinEntriesProvider {
   public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.CONFIGURED_FEATURE, AetherConfiguredFeatures::bootstrap)
      .add(Registries.PLACED_FEATURE, AetherPlacedFeatures::bootstrap)
      .add(Registries.BIOME, AetherBiomes::bootstrap)
      .add(Registries.DENSITY_FUNCTION, AetherDensityFunctions::bootstrap)
      .add(Registries.NOISE, AetherNoises::bootstrap)
      .add(Registries.NOISE_SETTINGS, AetherNoiseSettings::bootstrap)
      .add(Registries.DIMENSION_TYPE, AetherDimensions::bootstrapDimensionType)
      .add(Registries.LEVEL_STEM, AetherDimensions::bootstrapLevelStem)
      .add(Registries.STRUCTURE, AetherStructures::bootstrap)
      .add(Registries.PROCESSOR_LIST, AetherStructureProcessorLists::bootstrap)
      .add(Registries.STRUCTURE_SET, AetherStructureSets::bootstrap)
      .add(Registries.DAMAGE_TYPE, AetherDamageTypes::bootstrap)
      .add(Registries.TRIM_MATERIAL, AetherTrimMaterials::bootstrap)
      .add(Registries.JUKEBOX_SONG, AetherJukeboxSongs::bootstrap)
      .add(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY, AetherMoaTypes::bootstrap);

   public AetherRegistrySets(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries, BUILDER, Collections.singleton("aether"));
   }
}
