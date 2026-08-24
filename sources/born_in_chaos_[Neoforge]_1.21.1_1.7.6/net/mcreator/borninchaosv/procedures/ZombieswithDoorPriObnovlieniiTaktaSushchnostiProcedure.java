package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.DoorKnightEntity;
import net.mcreator.borninchaosv.entity.DoorKnightNotDespawnEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class ZombieswithDoorPriObnovlieniiTaktaSushchnostiProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity());
      }
   }

   public static void execute(Entity entity) {
      execute(null, entity);
   }

   private static void execute(@Nullable Event event, Entity entity) {
      if (entity != null) {
         if (entity instanceof DoorKnightEntity
            && entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && !(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
            && !entity.isOnFire()
            && (new Object() {
               public int getScore(String score, Entity _ent) {
                  Scoreboard _sc = _ent.level().getScoreboard();
                  Objective _so = _sc.getObjective(score);
                  return _so != null ? _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _so).get() : 0;
               }
            }).getScore("bloc", entity) == 1) {
            Scoreboard _sc = entity.level().getScoreboard();
            Objective _so = _sc.getObjective("bloc");
            if (_so == null) {
               _so = _sc.addObjective("bloc", ObjectiveCriteria.DUMMY, Component.literal("bloc"), RenderType.INTEGER, true, null);
            }

            _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _so).set(0);
            if (entity instanceof DoorKnightEntity) {
               ((DoorKnightEntity)entity).setAnimation("block");
            }
         }

         if (entity instanceof DoorKnightNotDespawnEntity
            && entity instanceof LivingEntity _livEnt8
            && _livEnt8.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && !(entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
            && !entity.isOnFire()
            && (new Object() {
               public int getScore(String score, Entity _ent) {
                  Scoreboard _scx = _ent.level().getScoreboard();
                  Objective _sox = _scx.getObjective(score);
                  return _sox != null ? _scx.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _sox).get() : 0;
               }
            }).getScore("bloc", entity) == 1) {
            Scoreboard _scx = entity.level().getScoreboard();
            Objective _sox = _scx.getObjective("bloc");
            if (_sox == null) {
               _sox = _scx.addObjective("bloc", ObjectiveCriteria.DUMMY, Component.literal("bloc"), RenderType.INTEGER, true, null);
            }

            _scx.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _sox).set(0);
            if (entity instanceof DoorKnightNotDespawnEntity) {
               ((DoorKnightNotDespawnEntity)entity).setAnimation("block");
            }
         }
      }
   }
}
