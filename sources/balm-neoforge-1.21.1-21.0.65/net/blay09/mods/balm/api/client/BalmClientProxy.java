package net.blay09.mods.balm.api.client;

import net.blay09.mods.balm.api.BalmProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class BalmClientProxy extends BalmProxy {
   @Nullable
   @Override
   public Player getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   @Override
   public boolean isLocalServer() {
      Minecraft client = Minecraft.getInstance();
      return client != null && client.isLocalServer();
   }

   @Override
   public boolean isConnected() {
      Minecraft client = Minecraft.getInstance();
      return client != null && client.getConnection() != null;
   }

   @Override
   public boolean isIngame() {
      Minecraft client = Minecraft.getInstance();
      return client != null && client.gameMode != null;
   }

   @Override
   public boolean isClient() {
      return true;
   }

   @Nullable
   @Override
   public Connection getConnection() {
      ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
      return packetListener != null ? packetListener.getConnection() : null;
   }

   @Nullable
   @Override
   public ClientGamePacketListener getPacketListener() {
      return Minecraft.getInstance().getConnection();
   }
}
