package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnsBiomeModifier;

public record RemoveBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs)
   implements WorldgenModifier,
   NeoforgeModifierHolder {
   public static final MapCodec<RemoveBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_REMOVE_CODEC.forGetter(RemoveBiomeSpawnsModifier::priority),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveBiomeSpawnsModifier::biomes),
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("mobs").forGetter(RemoveBiomeSpawnsModifier::mobs)
         )
         .apply(instance, RemoveBiomeSpawnsModifier::new)
   );

   @Override
   public BiomeModifier createNeoforgeModifier() {
      return new RemoveSpawnsBiomeModifier(this.biomes, this.mobs);
   }

   @Override
   public void apply(RegistryAccess registries) {
   }

   public void applyModifier(Biome biome) {
      MobSpawnSettings biomeMobSettings = biome.getMobSettings();
      HashMap<MobCategory, WeightedRandomList<SpawnerData>> spawners = new HashMap<>(((MobSpawnSettingsAccessor)biomeMobSettings).getSpawners());

      for (MobCategory category : MobCategory.values()) {
         List<SpawnerData> categorySpawnList = new ArrayList<>(spawners.get(category).unwrap());
         categorySpawnList.removeIf(mobEntry -> this.mobs.contains(mobEntry.type.builtInRegistryHolder()));
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
