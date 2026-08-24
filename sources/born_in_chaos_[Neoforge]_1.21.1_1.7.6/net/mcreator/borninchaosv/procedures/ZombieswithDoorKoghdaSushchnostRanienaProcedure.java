package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.DoorKnightEntity;
import net.mcreator.borninchaosv.entity.DoorKnightNotDespawnEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;

public class ZombieswithDoorKoghdaSushchnostRanienaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((entity instanceof DoorKnightEntity || entity instanceof DoorKnightNotDespawnEntity)
            && (sourceentity instanceof Player || sourceentity instanceof Mob || sourceentity instanceof Monster)
            && !entity.isOnFire()
            && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) >= 5.0F
            && !entity.isInWall()
            && world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() != Blocks.MAGMA_BLOCK) {
            if (!(entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
               && !(entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(MobEffects.DAMAGE_RESISTANCE))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 3, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 2, false, false));
               }
            }

            if (entity instanceof LivingEntity _livEnt14
               && _livEnt14.hasEffect(MobEffects.DAMAGE_RESISTANCE)
               && !(entity instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
               && !world.isClientSide()) {
               if (Math.random() < 0.45) {
                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:doorblokc")),
                           SoundSource.NEUTRAL,
                           0.6F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:doorblokc")),
                           SoundSource.NEUTRAL,
                           0.6F,
                           1.0F,
                           false
                        );
                     }
                  }
               } else if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:doorblokc2")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:doorblokc2")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            Scoreboard _sc = entity.level().getScoreboard();
            Objective _so = _sc.getObjective("bloc");
            if (_so == null) {
               _so = _sc.addObjective("bloc", ObjectiveCriteria.DUMMY, Component.literal("bloc"), RenderType.INTEGER, true, null);
            }

            _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _so).set(1);
         }
      }
   }
}
