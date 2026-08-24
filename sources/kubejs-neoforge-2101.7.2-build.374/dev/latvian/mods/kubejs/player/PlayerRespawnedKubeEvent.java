package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.server.level.ServerPlayer;

@Info("Invoked when a player respawns.\n\nThe reason of respawn can be either death or returning from the end.\n")
public class PlayerRespawnedKubeEvent implements KubePlayerEvent {
   private final ServerPlayer player;
   private final boolean endConquered;

   public PlayerRespawnedKubeEvent(ServerPlayer player, boolean endConquered) {
      this.player = player;
      this.endConquered = endConquered;
   }

   @Info("Gets the player that respawned.")
   public ServerPlayer getEntity() {
      return this.player;
   }

   public boolean isEndConquered() {
      return this.endConquered;
   }
}
