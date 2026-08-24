package net.mcreator.undeadrevamp.procedures;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.network.UndeadRevamp2ModVariables;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber
public class FallinmayloveProcedure {
   @SubscribeEvent
   public static void onEntityFall(LivingFallEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity(), event.getDistance());
      }
   }

   public static void execute(Entity entity, double distance) {
      execute(null, entity, distance);
   }

   private static void execute(@Nullable Event event, Entity entity, double distance) {
      if (entity != null) {
         Entity fallin_mylove = null;
         UndeadRevamp2ModVariables.PlayerVariables _vars = (UndeadRevamp2ModVariables.PlayerVariables)entity.getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES);
         _vars.fallinmylove = distance;
         _vars.syncPlayerVariables(entity);
      }
   }
}
