package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class DetonationKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y + 1.5, z, 1, 0.2, 0.1, 0.2, 0.1);
         }

         if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
            && (
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.DETONATION)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.DETONATION).getDuration()
                     : 0
               )
               <= 20) {
            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, x, y, z, 3.0F, ExplosionInteraction.TNT);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.DETONATION);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 60, 0, false, false));
            }
         } else if (!world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
            && (
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.DETONATION)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.DETONATION).getDuration()
                     : 0
               )
               <= 20) {
            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, x, y, z, 3.0F, ExplosionInteraction.NONE);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.DETONATION);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 60, 0, false, false));
            }
         }
      }
   }
}
