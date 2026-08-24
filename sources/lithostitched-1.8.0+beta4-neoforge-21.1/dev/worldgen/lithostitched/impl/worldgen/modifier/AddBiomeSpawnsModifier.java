package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier;

public record AddBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, List<WeightedSpawnerData> biomeSpawns)
   implements WorldgenModifier,
   NeoforgeModifierHolder {
   public static final MapCodec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(AddBiomeSpawnsModifier::priority),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
            Codec.mapEither(WeightedSpawnerData.CODEC.listOf().fieldOf("spawners"), WeightedSpawnerData.CODEC.fieldOf("spawners"))
               .xmap(either -> (List)either.map(list -> list, List::of), Either::left)
               .forGetter(AddBiomeSpawnsModifier::biomeSpawns)
         )
         .apply(instance, AddBiomeSpawnsModifier::new)
   );

   @Override
   public BiomeModifier createNeoforgeModifier() {
      return new AddSpawnsBiomeModifier(
         this.biomes, this.biomeSpawns.stream().map(data -> new SpawnerData(data.type(), data.weight(), data.minCount(), data.maxCount())).toList()
      );
   }

   @Override
   public void apply(RegistryAccess registries) {
   }

   public void applyModifier(Biome biome) {
      MobSpawnSettings biomeMobSettings = biome.getMobSettings();
      HashMap<MobCategory, WeightedRandomList<SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());

      for (WeightedSpawnerData spawner : this.biomeSpawns()) {
         MobCategory category = spawner.type().getCategory();
         List<SpawnerData> categorySpawnList = new ArrayList<>(spawners.get(category).unwrap());
         categorySpawnList.add(new SpawnerData(spawner.type(), spawner.weight(), spawner.minCount(), spawner.maxCount()));
         spawners.put(category, WeightedRandomList.create(categorySpawnList));
      }

      ((MobSpawnSettingsAccessor)biomeMobSettings).setSpawners(spawners);
      ((BiomeAccessor)biome).setMobSettings(biomeMobSettings);
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
