package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.ThebidyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class ThebidyOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof ThebidyEntity animatable) {
            animatable.setTexture("emptytexture");
         }

         if (!entity.isInWaterOrBubble()) {
            entity.getPersistentData().putBoolean("noatk", true);
            if (entity instanceof ThebidyEntity) {
               ((ThebidyEntity)entity).setAnimation("pop");
            }

            UndeadRevamp2Mod.queueServerWork(4, () -> {
               if (entity instanceof ThebidyEntity animatablex) {
                  animatablex.setTexture("bidy");
               }
            });
            UndeadRevamp2Mod.queueServerWork(9, () -> {
               if (entity instanceof ThebidyEntity) {
                  ((ThebidyEntity)entity).setAnimation("pop");
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 24, 30, false, false));
               }
            });
            UndeadRevamp2Mod.queueServerWork(
               14, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(24, () -> entity.getPersistentData().putBoolean("noatk", false));
         }
      }
   }
}
