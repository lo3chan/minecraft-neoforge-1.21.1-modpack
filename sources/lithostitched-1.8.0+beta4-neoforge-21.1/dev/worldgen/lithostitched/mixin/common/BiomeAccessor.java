package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.Biome.ClimateSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Biome.class})
public interface BiomeAccessor {
   @Accessor("climateSettings")
   @Mutable
   ClimateSettings getClimateSettings();

   @Accessor("climateSettings")
   @Mutable
   void setClimateSettings(ClimateSettings var1);

   @Accessor("specialEffects")
   BiomeSpecialEffects getSpecialEffects();

   @Accessor("specialEffects")
   @Mutable
   void setSpecialEffects(BiomeSpecialEffects var1);

   @Accessor("generationSettings")
   @Mutable
   void setGenerationSettings(BiomeGenerationSettings var1);

   @Accessor("mobSettings")
   @Mutable
   void setMobSettings(MobSpawnSettings var1);
}
