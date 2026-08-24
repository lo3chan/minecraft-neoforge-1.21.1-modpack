package dev.latvian.mods.kubejs.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;

public class PlayerChatReceivedKubeEvent implements KubePlayerEvent {
   private final ServerChatEvent event;

   public PlayerChatReceivedKubeEvent(ServerChatEvent event) {
      this.event = event;
   }

   public ServerPlayer getEntity() {
      return this.event.getPlayer();
   }

   public String getUsername() {
      return this.event.getPlayer().getGameProfile().getName();
   }

   public String getMessage() {
      return this.event.getRawText();
   }

   public Component getComponent() {
      return this.event.getMessage();
   }

   public void setComponent(Component component) {
      this.event.setMessage(component);
   }
}
