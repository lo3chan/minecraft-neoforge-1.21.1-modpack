package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TestProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.isShiftKeyDown()
            && !entity.isSprinting()
            && !entity.getPersistentData().getBoolean("inflat_anim")
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 60, 1, false, false));
         }
      }
   }
}
