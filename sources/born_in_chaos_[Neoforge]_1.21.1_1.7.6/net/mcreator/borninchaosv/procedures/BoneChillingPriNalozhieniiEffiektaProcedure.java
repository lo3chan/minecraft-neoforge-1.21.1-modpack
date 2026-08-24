package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BoneChillingPriNalozhieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                  ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                  : 0
            )
            <= 2) {
            entity.setTicksFrozen(80);
         } else if ((
                  entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                     ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                     : 0
               )
               > 2
            && (
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                     : 0
               )
               < 5) {
            entity.setTicksFrozen(170);
         } else if ((
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                  ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                  : 0
            )
            >= 5) {
            entity.setTicksFrozen(280);
         }
      }
   }
}
