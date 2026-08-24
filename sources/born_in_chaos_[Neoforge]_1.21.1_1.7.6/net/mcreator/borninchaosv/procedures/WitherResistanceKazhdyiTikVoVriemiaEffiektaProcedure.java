package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class WitherResistanceKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WITHER);
         }
      }
   }
}
