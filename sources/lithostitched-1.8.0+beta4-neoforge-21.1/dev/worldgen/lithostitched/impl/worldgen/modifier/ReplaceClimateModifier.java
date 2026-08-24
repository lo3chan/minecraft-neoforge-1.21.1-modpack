package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.ClimateSettings;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;

public record ReplaceClimateModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, BiomeClimate climateSettings)
   implements WorldgenModifier,
   NeoforgeModifierHolder {
   public static final MapCodec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(ReplaceClimateModifier::priority),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
            BiomeClimate.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
         )
         .apply(instance, ReplaceClimateModifier::new)
   );

   @Override
   public BiomeModifier createNeoforgeModifier() {
      return new LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier(this.biomes, this.climateSettings);
   }

   @Override
   public void apply(RegistryAccess registries) {
   }

   public void applyModifier(Biome biome) {
      ClimateSettings originalClimate = ((BiomeAccessor)biome).getClimateSettings();
      Boolean hasPrecipitation = this.climateSettings.hasPrecipitation().orElse(originalClimate.hasPrecipitation());
      Float temperature = this.climateSettings.temperature().orElse(originalClimate.temperature());
      TemperatureModifier temperatureModifier = this.climateSettings.temperatureModifier().orElse(originalClimate.temperatureModifier());
      Float downfall = this.climateSettings.downfall().orElse(originalClimate.downfall());
      ((BiomeAccessor)biome).setClimateSettings(new ClimateSettings(hasPrecipitation, temperature, temperatureModifier, downfall));
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
