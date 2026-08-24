package net.mcreator.undeadrevamp.procedures;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;

public class CoppertarRightClickedOnEntityProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.level().isClientSide()) {
            entity.discard();
         }

         if (world instanceof ServerLevel _level) {
            ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(Items.GUNPOWDER));
            entityToSpawn.setPickUpDelay(10);
            _level.addFreshEntity(entityToSpawn);
         }

         if (world instanceof ServerLevel _level) {
            _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 3));
         }
      }
   }
}
