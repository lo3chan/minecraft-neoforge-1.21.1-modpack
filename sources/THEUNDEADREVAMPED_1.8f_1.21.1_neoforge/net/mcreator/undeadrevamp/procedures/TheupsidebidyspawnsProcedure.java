package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.ThebidyupsideEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class TheupsidebidyspawnsProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putBoolean("upside", true);
         entity.getPersistentData().putBoolean("noatk", true);
         if (entity instanceof ThebidyupsideEntity animatable) {
            animatable.setTexture("emptytexture");
         }

         UndeadRevamp2Mod.queueServerWork(
            15,
            () -> {
               if (entity instanceof ThebidyupsideEntity) {
                  ((ThebidyupsideEntity)entity).setAnimation("upsidepop");
               }

               if (entity instanceof ThebidyupsideEntity animatablex) {
                  animatablex.setTexture("bidy");
               }

               UndeadRevamp2Mod.queueServerWork(
                  8, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y + 1.0, z))))
               );
               UndeadRevamp2Mod.queueServerWork(
                  12, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y + 1.0, z))))
               );
               UndeadRevamp2Mod.queueServerWork(14, () -> {
                  entity.getPersistentData().putBoolean("upside", false);
                  entity.getPersistentData().putBoolean("noatk", false);
               });
            }
         );
      }
   }
}
