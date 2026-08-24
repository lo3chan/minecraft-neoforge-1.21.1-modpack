package com.anthonyhilyard.iceberg.events.common;

import com.anthonyhilyard.iceberg.events.ToggleableEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.world.entity.player.Player;

public interface CriterionEvent {
   ToggleableEvent<CriterionEvent> EVENT = ToggleableEvent.create(CriterionEvent.class, listeners -> (player, advancementHolder, criterionKey) -> {
      for (CriterionEvent listener : listeners) {
         listener.awardCriterion(player, advancementHolder, criterionKey);
      }
   });

   void awardCriterion(Player var1, AdvancementHolder var2, String var3);
}
