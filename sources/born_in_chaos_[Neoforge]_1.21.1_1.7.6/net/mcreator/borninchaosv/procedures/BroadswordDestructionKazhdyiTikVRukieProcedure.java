package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BroadswordDestructionKazhdyiTikVRukieProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MobEffects.DAMAGE_BOOST))
            && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE))
            && !(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.MEDIUM_RAMPAGE))
            && !(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(BornInChaosV1ModMobEffects.STRONG_RAMPAGE))
            && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.FURIOUS_RAMPAGE))
            && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.RAMPANT_RAMPAGE))
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.OVERLY_HEAVY_WEAPON, 10, 0, false, false));
         }
      }
   }
}
