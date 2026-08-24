package net.mcreator.undeadrevamp.procedures;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class SkillissueProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(Entity entity, Entity sourceentity) {
      execute(null, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity instanceof Player
            && sourceentity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("undead_revamp2:undeadmobsmodded")))) {
            if ((new Object() {
               public int getScore(String score, Entity _ent) {
                  Scoreboard _sc = _ent.level().getScoreboard();
                  Objective _so = _sc.getObjective(score);
                  return _so != null ? _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _so).get() : 0;
               }
            }).getScore("skillissuecount", entity) > 5) {
               Scoreboard _sc = entity.level().getScoreboard();
               Objective _so = _sc.getObjective("skillissuecount");
               if (_so == null) {
                  _so = _sc.addObjective("skillissuecount", ObjectiveCriteria.DUMMY, Component.literal("skillissuecount"), RenderType.INTEGER, true, null);
               }

               _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _so).set((new Object() {
                  public int getScore(String score, Entity _ent) {
                     Scoreboard _scx = _ent.level().getScoreboard();
                     Objective _sox = _scx.getObjective(score);
                     return _sox != null ? _scx.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _sox).get() : 0;
                  }
               }).getScore("skillissuecount", entity) + 1);
               if (entity instanceof ServerPlayer _player) {
                  AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:nowhowdo_iconfigurethiscrap"));
                  if (_adv != null) {
                     AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                     if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria()) {
                           _player.getAdvancements().award(_adv, criteria);
                        }
                     }
                  }
               }
            } else {
               Scoreboard _scx = entity.level().getScoreboard();
               Objective _sox = _scx.getObjective("skillissuecount");
               if (_sox == null) {
                  _sox = _scx.addObjective("skillissuecount", ObjectiveCriteria.DUMMY, Component.literal("skillissuecount"), RenderType.INTEGER, true, null);
               }

               _scx.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _sox).set((new Object() {
                  public int getScore(String score, Entity _ent) {
                     Scoreboard _scxx = _ent.level().getScoreboard();
                     Objective _soxx = _scxx.getObjective(score);
                     return _soxx != null ? _scxx.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _soxx).get() : 0;
                  }
               }).getScore("skillissuecount", entity) + 1);
            }
         }
      }
   }
}
