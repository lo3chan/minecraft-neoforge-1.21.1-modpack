package dev.worldgen.lithostitched.platform.neoforge.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddSpawnCostsModifier;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public class LithostitchedNeoforgeBiomeModifiers {
   public record AddSpawnCostsBiomeModifier(AddSpawnCostsModifier modifier) implements BiomeModifier {
      public static final MapCodec<LithostitchedNeoforgeBiomeModifiers.AddSpawnCostsBiomeModifier> CODEC = AddSpawnCostsModifier.CODEC
         .xmap(LithostitchedNeoforgeBiomeModifiers.AddSpawnCostsBiomeModifier::new, LithostitchedNeoforgeBiomeModifiers.AddSpawnCostsBiomeModifier::modifier);

      public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
         if (phase == Phase.ADD && this.modifier.biomes().contains(biome)) {
            MobSpawnSettingsBuilder spawnSettings = builder.getMobSpawnSettings();

            for (Entry<EntityType<?>, MobSpawnCost> costEntry : this.modifier.spawnCosts().entrySet()) {
               spawnSettings.addMobCharge(costEntry.getKey(), costEntry.getValue().charge(), costEntry.getValue().energyBudget());
            }
         }
      }

      public MapCodec<? extends BiomeModifier> codec() {
         return CODEC;
      }
   }

   public record ReplaceClimateBiomeModifier(HolderSet<Biome> biomes, BiomeClimate climateSettings) implements BiomeModifier {
      public static final MapCodec<LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Biome.LIST_CODEC.fieldOf("biomes").forGetter(LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier::biomes),
               BiomeClimate.CODEC.fieldOf("climate").forGetter(LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier::climateSettings)
            )
            .apply(builder, LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier::new)
      );

      public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
         if (phase == Phase.MODIFY && this.biomes().contains(biome)) {
            ClimateSettingsBuilder climateSettings = builder.getClimateSettings();
            this.tryApply(this.climateSettings.temperature(), climateSettings::setTemperature);
            this.tryApply(this.climateSettings.temperatureModifier(), climateSettings::setTemperatureModifier);
            this.tryApply(this.climateSettings.hasPrecipitation(), climateSettings::setHasPrecipitation);
            this.tryApply(this.climateSettings.downfall(), climateSettings::setDownfall);
         }
      }

      private <T> void tryApply(Optional<T> value, Consumer<T> consumer) {
         value.ifPresent(consumer::accept);
      }

      public MapCodec<? extends BiomeModifier> codec() {
         return CODEC;
      }
   }

   public record ReplaceEffectsBiomeModifier(HolderSet<Biome> biomes, BiomeEffects effects) implements BiomeModifier {
      public static final MapCodec<LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Biome.LIST_CODEC.fieldOf("biomes").forGetter(LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier::biomes),
               BiomeEffects.CODEC.fieldOf("effects").forGetter(LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier::effects)
            )
            .apply(builder, LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier::new)
      );

      public void modify(Holder<Biome> biome, Phase phase, Builder info) {
         if (phase == Phase.MODIFY && this.biomes().contains(biome)) {
            BiomeSpecialEffectsBuilder builder = info.getSpecialEffects();
            this.tryApply(BiomeEffects::fogColor, builder::fogColor);
            this.tryApply(BiomeEffects::waterColor, builder::waterColor);
            this.tryApply(BiomeEffects::waterFogColor, builder::waterFogColor);
            this.tryApply(BiomeEffects::skyColor, builder::skyColor);
            this.tryApply(BiomeEffects::foliageColor, builder::foliageColorOverride);
            this.tryApply(BiomeEffects::grassColor, builder::grassColorOverride);
            this.tryApply(BiomeEffects::grassColorModifier, builder::grassColorModifier);
            this.tryApply(BiomeEffects::ambientParticle, builder::ambientParticle);
            this.tryApply(BiomeEffects::ambientSound, builder::ambientLoopSound);
            this.tryApply(BiomeEffects::moodSound, builder::ambientMoodSound);
            this.tryApply(BiomeEffects::additionsSound, builder::ambientAdditionsSound);
            this.tryApply(BiomeEffects::music, builder::backgroundMusic);
         }
      }

      private <T> void tryApply(Function<BiomeEffects, Optional<T>> getter, Consumer<T> applier) {
         getter.apply(this.effects).ifPresent(applier);
      }

      public MapCodec<? extends BiomeModifier> codec() {
         return CODEC;
      }
   }
}
