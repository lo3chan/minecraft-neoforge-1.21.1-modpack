package dev.worldgen.lithostitched.mixin.common;

import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({BiomeGenerationSettings.class})
public interface BiomeGenerationSettingsAccessor {
   @Invoker("<init>")
   static BiomeGenerationSettings createGenerationSettings(Map<Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers, List<HolderSet<PlacedFeature>> features) {
      throw new AssertionError();
   }

   @Accessor("carvers")
   @Mutable
   Map<Carving, HolderSet<ConfiguredWorldCarver<?>>> getCarvers();
}
