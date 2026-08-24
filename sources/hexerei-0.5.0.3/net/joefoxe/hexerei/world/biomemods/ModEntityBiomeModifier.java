package net.joefoxe.hexerei.world.biomemods;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public record ModEntityBiomeModifier(HolderSet<Biome> biomes, SpawnerData spawnerData) implements BiomeModifier {
   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD && this.biomes.contains(biome)) {
         builder.getMobSpawnSettings()
            .addSpawn(
               this.spawnerData.type.getCategory(),
               new SpawnerData(this.spawnerData.type, this.spawnerData.getWeight(), this.spawnerData.minCount, this.spawnerData.maxCount)
            );
      }
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return (MapCodec<? extends BiomeModifier>)ModBiomeModifiers.ENTITY_MODIFIER.get();
   }
}
