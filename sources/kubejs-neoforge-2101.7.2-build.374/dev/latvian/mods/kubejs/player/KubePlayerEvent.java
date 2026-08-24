package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface KubePlayerEvent extends KubeLivingEntityEvent {
   Player getEntity();

   @Nullable
   @Override
   default Player getPlayer() {
      return this.getEntity();
   }
}
