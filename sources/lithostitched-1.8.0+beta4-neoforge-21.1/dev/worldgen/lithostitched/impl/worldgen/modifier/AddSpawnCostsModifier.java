package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.SimpleMapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.neoforged.neoforge.common.world.BiomeModifier;

public record AddSpawnCostsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, Map<EntityType<?>, MobSpawnCost> spawnCosts)
   implements WorldgenModifier,
   NeoforgeModifierHolder {
   public static final SimpleMapCodec<EntityType<?>, MobSpawnCost> SPAWN_COST_CODEC = Codec.simpleMap(
      BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE
   );
   public static final MapCodec<AddSpawnCostsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(AddSpawnCostsModifier::priority),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnCostsModifier::biomes),
            SPAWN_COST_CODEC.fieldOf("spawn_costs").forGetter(AddSpawnCostsModifier::spawnCosts)
         )
         .apply(instance, AddSpawnCostsModifier::new)
   );

   @Override
   public BiomeModifier createNeoforgeModifier() {
      return new LithostitchedNeoforgeBiomeModifiers.AddSpawnCostsBiomeModifier(this);
   }

   @Override
   public void apply(RegistryAccess registries) {
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
