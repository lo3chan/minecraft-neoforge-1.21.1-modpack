package io.wispforest.owo.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientAccess implements OwoNetChannel.EnvironmentAccess<LocalPlayer, Minecraft, ClientPacketListener> {
   @OnlyIn(Dist.CLIENT)
   private final ClientPacketListener netHandler;
   @OnlyIn(Dist.CLIENT)
   private final Minecraft instance = Minecraft.getInstance();

   public ClientAccess(ClientPacketListener netHandler) {
      this.netHandler = netHandler;
   }

   @OnlyIn(Dist.CLIENT)
   public LocalPlayer player() {
      return this.instance.player;
   }

   @OnlyIn(Dist.CLIENT)
   public Minecraft runtime() {
      return this.instance;
   }

   @OnlyIn(Dist.CLIENT)
   public ClientPacketListener netHandler() {
      return this.netHandler;
   }
}
