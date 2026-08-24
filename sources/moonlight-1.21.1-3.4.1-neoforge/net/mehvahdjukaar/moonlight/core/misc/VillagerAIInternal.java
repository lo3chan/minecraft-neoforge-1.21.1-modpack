package net.mehvahdjukaar.moonlight.core.misc;

import net.mehvahdjukaar.moonlight.api.events.IVillagerBrainEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.core.misc.platform.VillagerAIInternalImpl;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;

public class VillagerAIInternal {
   public static void init() {
   }

   public static void onRegisterBrainGoals(Brain<Villager> brain, AbstractVillager villager) {
      if (villager instanceof Villager v) {
         IVillagerBrainEvent event = createEvent(brain, v);
         MoonlightEventsHelper.postEvent(event, IVillagerBrainEvent.class);
         VillagerBrainEventInternal internal = event.getInternal();
         if (internal.hasCustomSchedule()) {
            brain.setSchedule(internal.buildFinalizedSchedule());
            brain.updateActivityFromSchedule(villager.level().getDayTime(), villager.level().getGameTime());
         }
      }
   }

   public static IVillagerBrainEvent createEvent(Brain<Villager> var0, Villager var1) {
      return VillagerAIInternalImpl.createEvent(var0, var1);
   }
}
