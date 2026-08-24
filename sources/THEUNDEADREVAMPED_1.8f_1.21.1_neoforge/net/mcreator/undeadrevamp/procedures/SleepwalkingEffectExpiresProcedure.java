package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SleepwalkingEffectExpiresProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            AttributeInstance _attrInst = _entity.getAttribute(Attributes.STEP_HEIGHT);
            if (_attrInst != null) {
               _attrInst.setBaseValue(0.6000000238418579);
            }
         }
      }
   }
}
