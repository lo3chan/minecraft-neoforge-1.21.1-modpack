package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.TheordureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class TheordureOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("crackle", 0.0);
         if (world.getBiome(BlockPos.containing(x, y, z)).is(ResourceLocation.parse("lush_caves"))) {
            if (entity instanceof TheordureEntity animatable) {
               animatable.setTexture("lushcaveordure");
            }
         } else {
            if (world.getBlockState(BlockPos.containing(x, y + 1.0, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:mineable/axe")))
               && entity instanceof TheordureEntity animatable) {
               animatable.setTexture("woodyordure");
            }

            if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:mineable/axe")))
               && entity instanceof TheordureEntity animatable) {
               animatable.setTexture("woodyordure");
            }
         }
      }
   }
}
