package dev.corgitaco.enhancedcelestials2core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.BiomeSpecialEffects.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({NaturalSpawner.class})
public class NaturalSpawnerMixin {
   @ModifyReturnValue(
      method = {"mobsAt"},
      at = {@At("RETURN")}
   )
   private static WeightedRandomList<SpawnerData> useLunarSpawner(
      WeightedRandomList<SpawnerData> original,
      ServerLevel world,
      StructureManager $$1,
      ChunkGenerator $$2,
      MobCategory classification,
      BlockPos $$4,
      Holder<Biome> $$5
   ) {
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         return original;
      } else {
         LunarEvent lunarEvent = lunarForecastWorldData.orElseThrow().currentLunarEvent();
         Optional<MobSpawnSettings> lunarSpawnSettings = lunarEvent.mobSpawnSettings();
         if (lunarSpawnSettings.isEmpty()) {
            return original;
         } else {
            MobSpawnSettings mobSpawnInfo = lunarSpawnSettings.orElseThrow();
            if (lunarEvent.useBiomeSpawnSettings()) {
               List<SpawnerData> unwrap = new ArrayList<>(mobSpawnInfo.getMobs(classification).unwrap());
               unwrap.addAll(original.unwrap());
               return WeightedRandomList.create(unwrap);
            } else {
               return mobSpawnInfo.getMobs(classification);
            }
         }
      }
   }

   @ModifyExpressionValue(
      method = {"isValidPositionForMob"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/MobCategory;getDespawnDistance()I"
      )}
   )
   private static int applyLunarMobSpawnDistance(int original, ServerLevel level, Mob mob, double distanceToNearestPlayerSqr) {
      return EnhancedCelestials.lunarForecastWorldData(level)
         .map(data -> data.currentLunarEvent().mobSpawnDistances().getOrDefault(mob.getType().getCategory(), original))
         .orElse(original);
   }

   @ModifyReturnValue(
      method = {"getRoughBiome"},
      at = {@At("RETURN")}
   )
   private static Biome useLunarSpawner(Biome original, BlockPos pos, ChunkAccess chunk) {
      if (chunk instanceof LevelChunk levelChunk) {
         Level world = levelChunk.getLevel();
         Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
         if (lunarForecastWorldData.isEmpty()) {
            return original;
         } else {
            LunarEvent lunarEvent = lunarForecastWorldData.orElseThrow().currentLunarEvent();
            Optional<MobSpawnSettings> lunarSpawnSettings = lunarEvent.mobSpawnSettings();
            if (lunarSpawnSettings.isEmpty()) {
               return original;
            } else {
               MobSpawnSettings lunarMobSpawnInfo = lunarSpawnSettings.orElseThrow();
               BiomeBuilder fakeBiome = new BiomeBuilder()
                  .hasPrecipitation(false)
                  .temperature(0.5F)
                  .downfall(0.5F)
                  .specialEffects(
                     new Builder()
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(1)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .build()
                  );
               if (lunarEvent.useBiomeSpawnSettings()) {
                  MobSpawnSettings biomeMobSpawnInfo = original.getMobSettings();
                  EnumMap<MobCategory, WeightedRandomList<SpawnerData>> mergedSpawnersMap = new EnumMap<>(MobCategory.class);
                  biomeMobSpawnInfo.spawners.forEach(mergedSpawnersMap::put);
                  lunarMobSpawnInfo.spawners.forEach(mergedSpawnersMap::put);
                  IdentityHashMap<EntityType<?>, MobSpawnCost> mergedSpawnCosts = new IdentityHashMap<>(biomeMobSpawnInfo.mobSpawnCosts);
                  mergedSpawnCosts.putAll(lunarMobSpawnInfo.mobSpawnCosts);
                  MobSpawnSettings mobSpawnInfo = new MobSpawnSettings(
                     Math.max(lunarMobSpawnInfo.getCreatureProbability(), biomeMobSpawnInfo.getCreatureProbability()), mergedSpawnersMap, mergedSpawnCosts
                  );
                  fakeBiome.mobSpawnSettings(mobSpawnInfo);
               } else {
                  fakeBiome.mobSpawnSettings(lunarMobSpawnInfo);
               }

               fakeBiome.generationSettings(BiomeGenerationSettings.EMPTY);
               return fakeBiome.build();
            }
         }
      } else {
         return original;
      }
   }

   @ModifyReturnValue(
      method = {"getRandomPosWithin"},
      at = {@At("RETURN")}
   )
   private static BlockPos forceSurface(BlockPos original, Level world, LevelChunk chunk) {
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (!lunarForecastWorldData.isEmpty() && lunarForecastWorldData.orElseThrow().currentLunarEvent().forceSurfaceSpawning()) {
         Player closestPlayer = world.getNearestPlayer(original.getX(), original.getY(), original.getZ(), -1.0, false);
         if (closestPlayer == null) {
            return original;
         } else {
            BlockPos closestPlayerPosition = closestPlayer.blockPosition();
            return closestPlayerPosition.getY() > world.getHeight(Types.WORLD_SURFACE, closestPlayerPosition.getX(), closestPlayerPosition.getZ())
               ? new BlockPos(original.getX(), world.getHeight(Types.WORLD_SURFACE, original.getX(), original.getZ()) + 1, original.getZ())
               : original;
         }
      } else {
         return original;
      }
   }
}
