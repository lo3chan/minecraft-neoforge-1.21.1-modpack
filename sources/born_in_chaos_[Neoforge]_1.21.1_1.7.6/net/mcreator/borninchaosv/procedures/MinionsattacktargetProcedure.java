package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.ControlledBabySkeletonEntity;
import net.mcreator.borninchaosv.entity.ControlledSpiritualAssistantEntity;
import net.mcreator.borninchaosv.entity.MrPumpkinControlledEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber
public class MinionsattacktargetProcedure {
   @SubscribeEvent
   public static void onEntitySetsAttackTarget(LivingChangeTargetEvent event) {
      execute(event, event.getOriginalAboutToBeSetTarget(), event.getEntity());
   }

   public static void execute(Entity entity, Entity sourceentity) {
      execute(null, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((
               sourceentity instanceof ControlledBabySkeletonEntity
                  || sourceentity instanceof ControlledSpiritualAssistantEntity
                  || sourceentity instanceof MrPumpkinControlledEntity
            )
            && entity instanceof TamableAnimal _tamEntx
            && _tamEntx.isTame()
            && sourceentity instanceof TamableAnimal _tamEnt
            && _tamEnt.isTame()) {
            sourceentity.getPersistentData().putBoolean("attack_target", true);
         } else if ((
               sourceentity instanceof ControlledBabySkeletonEntity
                  || sourceentity instanceof ControlledSpiritualAssistantEntity
                  || sourceentity instanceof MrPumpkinControlledEntity
            )
            && !(entity instanceof TamableAnimal _tamEntx && _tamEntx.isTame())
            && sourceentity instanceof TamableAnimal _tamEnt
            && _tamEnt.isTame()) {
            sourceentity.getPersistentData().putBoolean("attack_target", false);
         }
      }
   }
}
