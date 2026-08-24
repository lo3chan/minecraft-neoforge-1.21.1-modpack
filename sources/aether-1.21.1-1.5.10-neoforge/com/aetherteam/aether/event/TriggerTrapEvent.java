package com.aetherteam.aether.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class TriggerTrapEvent extends BlockEvent implements ICancellableEvent {
   private final Player player;

   public TriggerTrapEvent(Player player, LevelAccessor level, BlockPos pos, BlockState state) {
      super(level, pos, state);
      this.player = player;
   }

   public Player getPlayer() {
      return this.player;
   }
}
