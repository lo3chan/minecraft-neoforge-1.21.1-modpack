package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class ThebeartamerNaturalEntitySpawningConditionProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return (
            (world instanceof Level _lvlxx ? _lvlxx.dimension() : (world instanceof WorldGenLevel _wglxx ? _wglxx.getLevel().dimension() : Level.OVERWORLD))
                     == Level.OVERWORLD
                  && world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) > 78
               || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.SNOW
                  && (
                        world instanceof Level _lvlx
                           ? _lvlx.dimension()
                           : (world instanceof WorldGenLevel _wglx ? _wglx.getLevel().dimension() : Level.OVERWORLD)
                     )
                     == Level.OVERWORLD
               || (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
                     == Level.OVERWORLD
                  && world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SNOW_BLOCK
         )
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("deep_dark"))
         && !world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("mushroom_fields"))
         && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 7;
   }
}
