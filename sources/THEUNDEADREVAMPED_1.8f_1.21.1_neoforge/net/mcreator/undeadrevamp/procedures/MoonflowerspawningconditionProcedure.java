package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;

public class MoonflowerspawningconditionProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return (
            (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
                  == Level.OVERWORLD
               || world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:overworld")))
         )
         && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 7
         && world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() != Blocks.INFESTED_DEEPSLATE;
   }
}
