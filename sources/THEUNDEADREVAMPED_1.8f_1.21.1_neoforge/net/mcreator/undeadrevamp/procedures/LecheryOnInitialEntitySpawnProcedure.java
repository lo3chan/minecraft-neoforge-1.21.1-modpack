package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class LecheryOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LecheryEntity animatable) {
            animatable.setTexture("emptytexture");
         }

         if (!entity.isInWaterOrBubble()) {
            entity.getPersistentData().putBoolean("noatk", true);
            if (entity instanceof LecheryEntity) {
               ((LecheryEntity)entity).setAnimation("rise");
            }

            UndeadRevamp2Mod.queueServerWork(3, () -> {
               if (entity instanceof LecheryEntity animatablex) {
                  animatablex.setTexture("the_lechery");
               }
            });
            UndeadRevamp2Mod.queueServerWork(
               5, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(20, () -> {
               world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))));
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 32, false, false));
               }
            });
            UndeadRevamp2Mod.queueServerWork(40, () -> {
               entity.getPersistentData().putBoolean("noatk", false);
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FULMINATION, 250, 0, false, false));
               }
            });
         }
      }
   }
}
