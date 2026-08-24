package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EvilometerKazhdyiTikVInvientarieProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 90.0
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0, false, false));
         }
      }
   }
}
