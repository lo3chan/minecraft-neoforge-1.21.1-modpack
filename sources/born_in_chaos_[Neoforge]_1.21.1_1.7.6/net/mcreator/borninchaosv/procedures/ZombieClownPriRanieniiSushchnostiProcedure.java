package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ZombieClownPriRanieniiSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               <= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) - 10.0F
            && !(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.STIMULATION))
            && !entity.isOnFire()
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STIMULATINGSURGE, 20, 0, false, false));
         }
      }
   }
}
