package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class KillingVillagersProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity(),
            event.getSource().getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof Player
            && (entity instanceof Villager || entity instanceof WanderingTrader)
            && world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.NAUGHTINESS_MECHANICS)) {
            if (entity instanceof LivingEntity _livEnt4 && _livEnt4.isBaby()) {
               BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(
                  BornInChaosV1ModVariables.PLAYER_VARIABLES
               );
               _vars.naughtiness = ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness
                  + 15.0;
               _vars.syncPlayerVariables(sourceentity);
            } else {
               BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(
                  BornInChaosV1ModVariables.PLAYER_VARIABLES
               );
               _vars.naughtiness = ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness
                  + 10.0;
               _vars.syncPlayerVariables(sourceentity);
            }

            if (!sourceentity.getPersistentData().getBoolean("firstwarning")
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 35.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 50.0) {
               sourceentity.getPersistentData().putBoolean("firstwarning", true);
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
            } else if (!sourceentity.getPersistentData().getBoolean("secondwarning")
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 65.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 75.0) {
               sourceentity.getPersistentData().putBoolean("secondwarning", true);
               sourceentity.getPersistentData().putBoolean("firstwarning", false);
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
            } else if (!sourceentity.getPersistentData().getBoolean("finalwarning")
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 85.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 95.0) {
               sourceentity.getPersistentData().putBoolean("finalwarning", true);
               sourceentity.getPersistentData().putBoolean("secondwarning", false);
               sourceentity.getPersistentData().putBoolean("firstwarning", false);
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

            if (sourceentity.getPersistentData().getDouble("punishment") == 0.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 100.0
               && world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY() - 1.0, sourceentity.getZ())).canOcclude()
               && world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY() + 1.0, sourceentity.getZ())).getBlock() == Blocks.AIR
               && (
                  world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ())).getBlock() == Blocks.AIR
                     || world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ())).getBlock() == Blocks.SNOW
                     || !world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ())).canOcclude()
               )) {
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false));
               }

               if (world instanceof ServerLevel _levelxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.KRAMPUS.get())
                     .spawn(_levelxxx, BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ()), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               sourceentity.getPersistentData().putBoolean("finalwarning", false);
               sourceentity.getPersistentData().putDouble("punishment", 5.0);
               if (sourceentity instanceof ServerPlayer _player) {
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

            if (sourceentity.getPersistentData().getDouble("punishment") == 0.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 50.0
               && ((BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 65.0
               && world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY() - 1.0, sourceentity.getZ())).canOcclude()
               && (
                  world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ())).getBlock() == Blocks.AIR
                     || world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ())).getBlock() == Blocks.SNOW
                     || !world.getBlockState(BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ())).canOcclude()
               )
               && (
                  world.getBlockState(BlockPos.containing(sourceentity.getX() - 1.0, sourceentity.getY(), sourceentity.getZ())).getBlock() == Blocks.AIR
                     || world.getBlockState(BlockPos.containing(sourceentity.getX() - 1.0, sourceentity.getY(), sourceentity.getZ())).getBlock() == Blocks.SNOW
                     || !world.getBlockState(BlockPos.containing(sourceentity.getX() - 1.0, sourceentity.getY(), sourceentity.getZ())).canOcclude()
               )) {
               if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, 0, false, false));
               }

               sourceentity.getPersistentData().putDouble("punishment", 5.0);
               if (world instanceof ServerLevel _levelxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.KRAMPUS_HENCHMAN.get())
                     .spawn(_levelxxxx, BlockPos.containing(sourceentity.getX() + 1.0, sourceentity.getY(), sourceentity.getZ()), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.KRAMPUS_HENCHMAN.get())
                     .spawn(_levelxxxxx, BlockPos.containing(sourceentity.getX() - 1.0, sourceentity.getY(), sourceentity.getZ()), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }
            }

            if (sourceentity.getPersistentData().getDouble("punishment") > 0.0) {
               sourceentity.getPersistentData().putDouble("punishment", sourceentity.getPersistentData().getDouble("punishment") - 1.0);
            }
         }
      }
   }
}
