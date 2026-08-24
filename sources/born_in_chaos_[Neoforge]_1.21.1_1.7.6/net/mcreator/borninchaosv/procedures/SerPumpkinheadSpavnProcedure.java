package net.mcreator.borninchaosv.procedures;

import java.util.Calendar;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;

public class SerPumpkinheadSpavnProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SERPUMPKINHEADSPAWN)
         && world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SEASONAL_EVENTS)
         && (
            Calendar.getInstance().get(2) == 9 && Calendar.getInstance().get(5) >= 25 && Calendar.getInstance().get(5) <= 31
               || Calendar.getInstance().get(2) == 10 && Calendar.getInstance().get(5) >= 1 && Calendar.getInstance().get(5) <= 7
               || world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.HALLOWEEN_EVENT)
         )
         && (
            world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 4
               || !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z)) && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 0
         )
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("deep_dark"))
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("mushroom_fields"))
         && (
            world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:is_overworld")))
               || world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("terralith:all_terralith_biomes")))
         )
         && (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
            == Level.OVERWORLD;
   }
}
