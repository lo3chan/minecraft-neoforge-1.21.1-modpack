package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;

public class KrampusSpawnProProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return world.dayTime() >= 228400L
         && (
            world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 4
               || !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 0
         )
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("deep_dark"))
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("mushroom_fields"))
         && world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.KRAMPUS_SPAWN)
         && (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
            == Level.OVERWORLD
         && (
            world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:is_overworld")))
               || world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("terralith:all_terralith_biomes")))
         )
         && ((Biome)world.getBiome(BlockPos.containing(x, y, z)).value()).getBaseTemperature() * 100.0F <= 0.15;
   }
}
