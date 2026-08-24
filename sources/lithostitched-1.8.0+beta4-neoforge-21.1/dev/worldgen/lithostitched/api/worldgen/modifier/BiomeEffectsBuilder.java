package dev.worldgen.lithostitched.api.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.impl.worldgen.modifier.BiomeEffectsBuilderImpl;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier;

public interface BiomeEffectsBuilder {
   static BiomeEffectsBuilder create() {
      return new BiomeEffectsBuilderImpl();
   }

   BiomeEffectsBuilder fogColor(Integer var1);

   BiomeEffectsBuilder waterColor(Integer var1);

   BiomeEffectsBuilder waterFogColor(Integer var1);

   BiomeEffectsBuilder skyColor(Integer var1);

   BiomeEffectsBuilder foliageColor(Integer var1);

   BiomeEffectsBuilder dryFoliageColor(Integer var1);

   BiomeEffectsBuilder grassColor(Integer var1);

   BiomeEffectsBuilder grassColorModifier(GrassColorModifier var1);

   BiomeEffectsBuilder ambientParticle(AmbientParticleSettings var1);

   BiomeEffectsBuilder ambientSound(Holder<SoundEvent> var1);

   BiomeEffectsBuilder moodSound(AmbientMoodSettings var1);

   BiomeEffectsBuilder additionsSound(AmbientAdditionsSettings var1);

   BiomeEffectsBuilder music(Music var1);

   BiomeEffectsBuilder musicVolume(Float var1);

   BiomeEffects build();
}
