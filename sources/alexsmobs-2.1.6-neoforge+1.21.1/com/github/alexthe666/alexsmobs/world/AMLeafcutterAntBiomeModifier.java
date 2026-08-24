package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public class AMLeafcutterAntBiomeModifier implements BiomeModifier {
   public static Supplier<? extends MapCodec<? extends BiomeModifier>> SERIALIZER;
   private final HolderSet<PlacedFeature> features;

   public AMLeafcutterAntBiomeModifier(HolderSet<PlacedFeature> features) {
      this.features = features;
   }

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         AMWorldRegistry.addLeafcutterAntSpawns(biome, this.features, builder);
      }
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return (MapCodec<? extends BiomeModifier>)SERIALIZER.get();
   }

   public static MapCodec<AMLeafcutterAntBiomeModifier> makeCodec() {
      return RecordCodecBuilder.mapCodec(
         config -> config.group(PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(otherConfig -> otherConfig.features))
            .apply(config, AMLeafcutterAntBiomeModifier::new)
      );
   }
}
