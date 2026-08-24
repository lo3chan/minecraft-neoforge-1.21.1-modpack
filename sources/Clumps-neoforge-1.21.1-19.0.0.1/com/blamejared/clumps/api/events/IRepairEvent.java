package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;

public interface IRepairEvent {
   void setValue(int var1);

   int getValue();

   Player getPlayer();
}
