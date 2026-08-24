package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BostroxsetHelmetTickEventProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!entity.isSprinting()) {
            entity.setDeltaMovement(new Vec3(0.0, -2.0, 0.0));
         }

         if (entity.isSprinting() && entity instanceof Player _player) {
            _player.getFoodData().setSaturation((float)((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) - 0.05));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 0));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0));
         }
      }
   }
}
