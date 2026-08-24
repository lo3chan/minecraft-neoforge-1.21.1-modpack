package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SugarcoatlaysProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS);
   }
}
