package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;

public class RiverMintPriIspolzovaniiKostnoiMukiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         ItemEntity entityToSpawn = new ItemEntity(_level, x + 0.5, y + 0.5, z + 0.5, new ItemStack((ItemLike)BornInChaosV1ModBlocks.RIVER_MINT.get()));
         entityToSpawn.setPickUpDelay(10);
         _level.addFreshEntity(entityToSpawn);
      }
   }
}
