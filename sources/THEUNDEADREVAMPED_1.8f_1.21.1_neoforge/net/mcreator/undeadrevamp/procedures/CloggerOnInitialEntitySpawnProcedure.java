package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class CloggerOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("smashmode", 0.0);
         entity.getPersistentData().putDouble("rushmode", 1.0);
         entity.getPersistentData().putDouble("honeyman_a", 0.0);
         entity.getPersistentData().putDouble("eating", 0.0);
         entity.getPersistentData().putDouble("honeyman_b", 0.0);
         entity.getPersistentData().putDouble("honeyman_c", 0.0);
         entity.getPersistentData().putDouble("activatehitbox", 0.0);
         entity.getPersistentData().putDouble("explo", 0.0);
         entity.getPersistentData().putDouble("tt", 0.0);
         entity.getPersistentData().putDouble("passorsmash", 0.0);
         entity.getPersistentData().putDouble("pastat", 1.0);
         entity.getPersistentData().putDouble("inrange", 3.0);
         entity.getPersistentData().putDouble("wait", 1.0);
         if (entity instanceof LivingEntity _livingEntity13 && _livingEntity13.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD)) {
            _livingEntity13.getAttribute(UndeadRevamp2ModAttributes.RETURNVAULEUNDEAD).setBaseValue(1.0);
         }

         entity.getPersistentData().putBoolean("noatk", true);
         if (entity instanceof CloggerEntity animatable) {
            animatable.setTexture("emptytexture");
         }

         if (!entity.isInWaterOrBubble()) {
            entity.getPersistentData().putBoolean("noatk", true);
            if (entity instanceof CloggerEntity) {
               ((CloggerEntity)entity).setAnimation("pop");
            }

            UndeadRevamp2Mod.queueServerWork(4, () -> {
               if (entity instanceof CloggerEntity animatablex) {
                  animatablex.setTexture("theclogger");
               }
            });
            UndeadRevamp2Mod.queueServerWork(
               7,
               () -> {
                  world.levelEvent(
                     2001, BlockPos.containing(x + entity.getBbWidth(), y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001, BlockPos.containing(x, y, z + entity.getBbWidth()), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth(), y, z + entity.getBbWidth()),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth() - 1.0, y, z + entity.getBbWidth() + 1.0),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth() + 1.0, y, z + entity.getBbWidth() - 1.0),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 55, 30, false, false));
                  }
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               18,
               () -> {
                  world.levelEvent(
                     2001, BlockPos.containing(x + entity.getBbWidth(), y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001, BlockPos.containing(x, y, z + entity.getBbWidth()), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth(), y, z + entity.getBbWidth()),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth() - 1.0, y, z + entity.getBbWidth() + 1.0),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth() + 1.0, y, z + entity.getBbWidth() - 1.0),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
               }
            );
            UndeadRevamp2Mod.queueServerWork(
               38,
               () -> {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.break_block")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.break_block")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  world.levelEvent(
                     2001, BlockPos.containing(x + entity.getBbWidth(), y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001, BlockPos.containing(x, y, z + entity.getBbWidth()), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth(), y, z + entity.getBbWidth()),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth() - 1.0, y, z + entity.getBbWidth() + 1.0),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
                  world.levelEvent(
                     2001,
                     BlockPos.containing(x + entity.getBbWidth() + 1.0, y, z + entity.getBbWidth() - 1.0),
                     Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z)))
                  );
               }
            );
            UndeadRevamp2Mod.queueServerWork(60, () -> entity.getPersistentData().putBoolean("noatk", false));
            UndeadRevamp2Mod.queueServerWork(200, () -> entity.getPersistentData().putDouble("wait", 0.0));
         }
      }
   }
}
