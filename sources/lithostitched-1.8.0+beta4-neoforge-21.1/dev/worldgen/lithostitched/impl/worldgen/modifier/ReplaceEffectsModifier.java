package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.BiomeSpecialEffects.Builder;
import net.neoforged.neoforge.common.world.BiomeModifier;

public record ReplaceEffectsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, BiomeEffects effects)
   implements WorldgenModifier,
   NeoforgeModifierHolder {
   public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(ReplaceEffectsModifier::priority),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
            BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::effects)
         )
         .apply(instance, ReplaceEffectsModifier::new)
   );

   @Override
   public BiomeModifier createNeoforgeModifier() {
      return new LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier(this.biomes, this.effects);
   }

   @Override
   public void apply(RegistryAccess registries) {
   }

   public void applyModifier(Biome biome) {
      BiomeAccessor accessor = (BiomeAccessor)biome;
      BiomeSpecialEffects originalEffects = accessor.getSpecialEffects();
      Builder builder = new Builder();
      this.applyRequiredEffect(BiomeEffects::fogColor, originalEffects::getFogColor, builder::fogColor);
      this.applyRequiredEffect(BiomeEffects::waterColor, originalEffects::getWaterColor, builder::waterColor);
      this.applyRequiredEffect(BiomeEffects::waterFogColor, originalEffects::getWaterFogColor, builder::waterFogColor);
      this.applyRequiredEffect(BiomeEffects::skyColor, originalEffects::getSkyColor, builder::skyColor);
      this.applyOptionalEffect(BiomeEffects::foliageColor, originalEffects::getFoliageColorOverride, builder::foliageColorOverride);
      this.applyOptionalEffect(BiomeEffects::grassColor, originalEffects::getGrassColorOverride, builder::grassColorOverride);
      this.applyRequiredEffect(BiomeEffects::grassColorModifier, originalEffects::getGrassColorModifier, builder::grassColorModifier);
      this.applyOptionalEffect(BiomeEffects::ambientParticle, originalEffects::getAmbientParticleSettings, builder::ambientParticle);
      this.applyOptionalEffect(BiomeEffects::ambientSound, originalEffects::getAmbientLoopSoundEvent, builder::ambientLoopSound);
      this.applyOptionalEffect(BiomeEffects::moodSound, originalEffects::getAmbientMoodSettings, builder::ambientMoodSound);
      this.applyOptionalEffect(BiomeEffects::additionsSound, originalEffects::getAmbientAdditionsSettings, builder::ambientAdditionsSound);
      this.applyOptionalEffect(BiomeEffects::music, originalEffects::getBackgroundMusic, builder::backgroundMusic);
      accessor.setSpecialEffects(builder.build());
   }

   public <T> void applyRequiredEffect(Function<BiomeEffects, Optional<T>> getter, Supplier<T> fallback, Consumer<T> applier) {
      applier.accept(getter.apply(this.effects).orElse(fallback.get()));
   }

   public <T> void applyOptionalEffect(Function<BiomeEffects, Optional<T>> getter, Supplier<Optional<T>> fallback, Consumer<T> applier) {
      T value = getter.apply(this.effects).orElse(fallback.get().orElse(null));
      if (value != null) {
         applier.accept(value);
      }
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
