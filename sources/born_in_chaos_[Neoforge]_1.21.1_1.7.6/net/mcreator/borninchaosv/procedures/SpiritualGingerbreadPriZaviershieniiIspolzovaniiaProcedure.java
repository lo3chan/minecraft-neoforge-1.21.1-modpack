package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class SpiritualGingerbreadPriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION);
         }

         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.NAUGHTINESS_MECHANICS)) {
            BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)entity.getData(
               BornInChaosV1ModVariables.PLAYER_VARIABLES
            );
            _vars.naughtiness = ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness + 3.0;
            _vars.syncPlayerVariables(entity);
            if (!entity.getPersistentData().getBoolean("firstwarning")
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 35.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 50.0) {
               entity.getPersistentData().putBoolean("firstwarning", true);
               if (!world.isClientSide() && world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_alert_far")),
                        SoundSource.NEUTRAL,
                        0.7F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_alert_far")),
                        SoundSource.NEUTRAL,
                        0.7F,
                        1.0F,
                        false
                     );
                  }
               }
            } else if (!entity.getPersistentData().getBoolean("secondwarning")
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 65.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 75.0) {
               entity.getPersistentData().putBoolean("secondwarning", true);
               entity.getPersistentData().putBoolean("firstwarning", false);
               if (!world.isClientSide() && world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_alert")),
                        SoundSource.NEUTRAL,
                        0.7F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_alert")),
                        SoundSource.NEUTRAL,
                        0.7F,
                        1.0F,
                        false
                     );
                  }
               }
            } else if (!entity.getPersistentData().getBoolean("finalwarning")
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 85.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 95.0) {
               entity.getPersistentData().putBoolean("finalwarning", true);
               entity.getPersistentData().putBoolean("secondwarning", false);
               entity.getPersistentData().putBoolean("firstwarning", false);
               if (!world.isClientSide() && world instanceof Level _levelxx) {
                  if (!_levelxx.isClientSide()) {
                     _levelxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_alert_close")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _levelxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_alert_close")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            if (entity.getPersistentData().getDouble("punishment") == 0.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 100.0
               && world.getBlockState(BlockPos.containing(entity.getX() + 1.0, entity.getY() - 1.0, entity.getZ())).canOcclude()
               && world.getBlockState(BlockPos.containing(entity.getX() + 1.0, entity.getY() + 1.0, entity.getZ())).getBlock() == Blocks.AIR
               && (
                  !world.getBlockState(BlockPos.containing(entity.getX() + 1.0, entity.getY(), entity.getZ())).canOcclude()
                     || world.getBlockState(BlockPos.containing(entity.getX() + 1.0, entity.getY(), entity.getZ())).getBlock() == Blocks.AIR
                     || world.getBlockState(BlockPos.containing(entity.getX() + 1.0, entity.getY(), entity.getZ())).getBlock() == Blocks.SNOW
               )) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false));
               }

               if (world instanceof ServerLevel _levelxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.KRAMPUS.get())
                     .spawn(_levelxxx, BlockPos.containing(entity.getX() + 1.0, entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               entity.getPersistentData().putBoolean("finalwarning", false);
               entity.getPersistentData().putDouble("punishment", 5.0);
               if (entity instanceof ServerPlayer _player) {
                  AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:naughty_child"));
                  if (_adv != null) {
                     AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                     if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria()) {
                           _player.getAdvancements().award(_adv, criteria);
                        }
                     }
                  }
               }
            }

            if (entity.getPersistentData().getDouble("punishment") > 0.0) {
               entity.getPersistentData().putDouble("punishment", entity.getPersistentData().getDouble("punishment") - 1.0);
            }
         }
      }
   }
}
