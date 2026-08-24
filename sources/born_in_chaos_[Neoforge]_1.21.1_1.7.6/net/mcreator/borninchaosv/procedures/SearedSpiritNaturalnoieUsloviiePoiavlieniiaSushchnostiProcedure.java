package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;

public class SearedSpiritNaturalnoieUsloviiePoiavlieniiaSushchnostiProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return (
            world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 4
               || !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 0
         )
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("deep_dark"))
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("mushroom_fields"))
         && world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:has_structure/village_plains")));
   }
}
