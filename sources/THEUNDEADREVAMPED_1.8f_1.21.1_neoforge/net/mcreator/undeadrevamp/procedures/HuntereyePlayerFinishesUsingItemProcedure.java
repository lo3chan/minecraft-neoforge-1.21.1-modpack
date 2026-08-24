package net.mcreator.undeadrevamp.procedures;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.LevelAccessor;

public class HuntereyePlayerFinishesUsingItemProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 2));
      }
   }
}
