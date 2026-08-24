package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThehunterEntityDiesProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("horned") == 1.0) {
            if (world instanceof ServerLevel _level) {
               _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 4));
            }

            if (world instanceof Level _level) {
               BlockPos _bp = BlockPos.containing(x, y, z);
               if ((
                     BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), _level, _bp)
                        || BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL), _level, _bp, null)
                  )
                  && !_level.isClientSide()) {
                  _level.levelEvent(2005, _bp, 0);
               }
            }
         }

         if (entity.getPersistentData().getDouble("horned") == 2.0) {
            if (world instanceof ServerLevel _levelx) {
               _levelx.addFreshEntity(new ExperienceOrb(_levelx, x, y, z, 6));
            }

            if (world instanceof Level _levelx) {
               BlockPos _bp = BlockPos.containing(x, y, z);
               if ((
                     BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), _levelx, _bp)
                        || BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL), _levelx, _bp, null)
                  )
                  && !_levelx.isClientSide()) {
                  _levelx.levelEvent(2005, _bp, 0);
               }
            }
         }
      }
   }
}
