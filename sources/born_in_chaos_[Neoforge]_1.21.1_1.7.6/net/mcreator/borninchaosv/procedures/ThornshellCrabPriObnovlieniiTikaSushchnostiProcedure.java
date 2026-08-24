package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.ThornshellCrabEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;

public class ThornshellCrabPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((new Object() {
            public int getScore(String score, Entity _ent) {
               Scoreboard _sc = _ent.level().getScoreboard();
               Objective _so = _sc.getObjective(score);
               return _so != null ? _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _so).get() : 0;
            }
         }).getScore("eat", entity) == 1 && entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BARBEDATTACK)) {
            Scoreboard _sc = entity.level().getScoreboard();
            Objective _so = _sc.getObjective("eat");
            if (_so == null) {
               _so = _sc.addObjective("eat", ObjectiveCriteria.DUMMY, Component.literal("eat"), RenderType.INTEGER, true, null);
            }

            _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _so).set(0);
            if (entity instanceof ThornshellCrabEntity) {
               ((ThornshellCrabEntity)entity).setAnimation("eat");
            }
         }

         if (entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(MobEffects.SATURATION)) {
            if (entity.getPersistentData().getDouble("eati") == 0.0) {
               entity.getPersistentData().putDouble("eati", 10.0);
            } else {
               entity.getPersistentData().putDouble("eati", entity.getPersistentData().getDouble("eati") - 1.0);
            }

            if (entity.getPersistentData().getDouble("eati") == 0.0) {
               if (Math.random() < 0.35) {
                  if (world instanceof ServerLevel _level) {
                     ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack((ItemLike)BornInChaosV1ModItems.SPINY_SHELL.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _level.addFreshEntity(entityToSpawn);
                  }
               } else if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(Items.BONE_MEAL));
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }

               entity.getPersistentData().putDouble("eati", 0.0);
               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.SATURATION);
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt14 && _livEnt14.isBaby()) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.THORNSHELL_CRAB.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }
         }

         if (entity.isInWater() && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 3, false, false));
         }
      }
   }
}
