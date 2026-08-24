package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.TheimmortalEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class TheimmortalOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("burned", 0.0);
         entity.getPersistentData().putDouble("nore", 0.0);
         if (entity instanceof TheimmortalEntity animatable) {
            animatable.setTexture("emptytexture");
         }

         if (!entity.isInWaterOrBubble()) {
            entity.getPersistentData().putBoolean("noatk", true);
            if (entity instanceof TheimmortalEntity) {
               ((TheimmortalEntity)entity).setAnimation("digg");
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 30, false, false));
            }

            UndeadRevamp2Mod.queueServerWork(4, () -> {
               if (Math.random() < 0.028) {
                  entity.getPersistentData().putDouble("decored", 1.0);
               } else {
                  entity.getPersistentData().putDouble("decored", 0.0);
               }

               if (entity instanceof TheimmortalEntity animatable) {
                  animatable.setTexture("immortal");
               }
            });
            UndeadRevamp2Mod.queueServerWork(
               6, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(
               13, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(
               21, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(
               26, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(
               30, () -> world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))))
            );
            UndeadRevamp2Mod.queueServerWork(50, () -> entity.getPersistentData().putBoolean("noatk", false));
         }
      }
   }
}
