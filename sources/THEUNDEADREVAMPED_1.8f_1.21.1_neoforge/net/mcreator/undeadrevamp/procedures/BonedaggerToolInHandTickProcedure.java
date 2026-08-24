package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BonedaggerToolInHandTickProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.isSprinting() && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 2, false, false));
         }
      }
   }
}
