package net.blay09.mods.balm.api;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class BalmProxy {
   @Nullable
   public Player getClientPlayer() {
      return null;
   }

   public boolean isLocalServer() {
      return false;
   }

   public boolean isConnected() {
      return false;
   }

   public boolean isIngame() {
      return false;
   }

   public boolean isClient() {
      return false;
   }

   @Deprecated
   public final boolean isConnectedToServer() {
      return this.isConnected();
   }

   @Nullable
   public Connection getConnection() {
      return null;
   }

   @Nullable
   public ClientGamePacketListener getPacketListener() {
      return null;
   }
}
