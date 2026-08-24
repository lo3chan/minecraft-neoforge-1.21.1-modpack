package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CloggerarmorHelmetTickEventProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) >= 20) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 0, false, false));
            }
         }
      }
   }
}
