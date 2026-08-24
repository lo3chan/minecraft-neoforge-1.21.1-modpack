package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TransformingEasterCakePriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("transformativesnack") == 0.0) {
            entity.getPersistentData().putDouble("transformativesnack", Mth.nextInt(RandomSource.create(), 1, 3));
         }

         if (entity.getPersistentData().getDouble("transformativesnack") != 1.0
            || entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.PREDATORS_DESIRE)
            || entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.STONE_PERSISTENCE)) {
            if (entity.getPersistentData().getDouble("transformativesnack") != 2.0
               || entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(BornInChaosV1ModMobEffects.RABBIT_AGILITY)
               || entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(BornInChaosV1ModMobEffects.STONE_PERSISTENCE)) {
               if (entity.getPersistentData().getDouble("transformativesnack") == 3.0
                  && !(entity instanceof LivingEntity _livEnt14 && _livEnt14.hasEffect(BornInChaosV1ModMobEffects.RABBIT_AGILITY))
                  && !(entity instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(BornInChaosV1ModMobEffects.PREDATORS_DESIRE))
                  && entity instanceof LivingEntity _entity
                  && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STONE_PERSISTENCE, Mth.nextInt(RandomSource.create(), 12000, 36000), 0));
               }
            } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.PREDATORS_DESIRE, Mth.nextInt(RandomSource.create(), 12000, 36000), 0));
            }
         } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.RABBIT_AGILITY, Mth.nextInt(RandomSource.create(), 12000, 36000), 0));
         }
      }
   }
}
