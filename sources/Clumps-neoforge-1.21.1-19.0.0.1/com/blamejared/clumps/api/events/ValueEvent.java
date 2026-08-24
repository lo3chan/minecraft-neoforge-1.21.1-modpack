package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

public class ValueEvent extends Event implements IValueEvent {
   private final Player player;
   private int value;

   public ValueEvent(Player player, int value) {
      this.player = player;
      this.value = value;
   }

   @Override
   public void setValue(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   @Override
   public Player getPlayer() {
      return this.player;
   }
}
