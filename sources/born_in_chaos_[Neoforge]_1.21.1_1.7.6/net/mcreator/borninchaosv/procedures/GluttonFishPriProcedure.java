package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;

public class GluttonFishPriProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.GLUTTON_FISH_SPAWN)
         && (
            world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 4
               || !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 0
         )
         && (
            world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:has_structure/ocean_ruin_cold")))
               || world.getBiome(BlockPos.containing(x, y, z))
                  .is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:has_structure/ocean_ruin_warm")))
               || world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:has_structure/shipwreck")))
               || world.getBiome(BlockPos.containing(x, y, z))
                  .is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:has_structure/ocean_monument")))
               || world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("alexscaves:abyssal_chasm")))
               || world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("deep_ocean"))
         );
   }
}
