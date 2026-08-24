package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.typings.Info;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Info("Invoked when the debug info is rendered.\n")
public class DebugInfoKubeEvent extends ClientPlayerKubeEvent {
   private final List<String> lines;

   public DebugInfoKubeEvent(LocalPlayer player, List<String> l) {
      super(player);
      this.lines = l;
   }

   @Info("Whether the debug info should be rendered.")
   public boolean getShowDebug() {
      return Minecraft.getInstance().getDebugOverlay().showDebugScreen();
   }

   @Info("The lines of debug info. Mutating this list will change the debug info.")
   public List<String> getLines() {
      return this.lines;
   }
}
