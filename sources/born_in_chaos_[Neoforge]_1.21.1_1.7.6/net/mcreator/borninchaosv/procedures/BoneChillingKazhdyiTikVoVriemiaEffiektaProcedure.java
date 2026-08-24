package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BoneChillingKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity.isOnFire() || entity.isInLava()) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.BONE_CHILLING);
         }
      }
   }
}
