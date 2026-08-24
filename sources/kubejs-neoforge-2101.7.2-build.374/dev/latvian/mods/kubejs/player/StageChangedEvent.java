package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.world.entity.player.Player;

public class StageChangedEvent implements KubePlayerEvent {
   private final Player player;
   private final Stages stages;
   private final String stage;

   public StageChangedEvent(Player player, Stages stages, String stage) {
      this.player = player;
      this.stages = stages;
      this.stage = stage;
   }

   @Override
   public Player getEntity() {
      return this.player;
   }

   @Override
   public Player getPlayer() {
      return this.player;
   }

   public String getStage() {
      return this.stage;
   }

   public Stages getPlayerStages() {
      return this.stages;
   }
}
