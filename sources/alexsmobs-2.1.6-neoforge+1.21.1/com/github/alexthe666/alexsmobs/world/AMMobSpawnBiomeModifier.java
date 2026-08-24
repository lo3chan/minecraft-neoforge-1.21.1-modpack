package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public class AMMobSpawnBiomeModifier implements BiomeModifier {
   public static Supplier<? extends MapCodec<? extends BiomeModifier>> SERIALIZER;

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         AMWorldRegistry.addBiomeSpawns(biome, builder);
      }
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return (MapCodec<? extends BiomeModifier>)SERIALIZER.get();
   }

   public static MapCodec<AMMobSpawnBiomeModifier> makeCodec() {
      return MapCodec.unit(AMMobSpawnBiomeModifier::new);
   }
}
