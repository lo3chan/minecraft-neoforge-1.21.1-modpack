package mezz.jei.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.Nullable;

public final class ClientConnectionHelper {
   private static final String UNKNOWN_SERVER_BRAND = "unknown";

   private ClientConnectionHelper() {
   }

   public static String getServerBrand() {
      ClientPacketListener clientPacketListener = getConnectedClientPacketListener();
      if (clientPacketListener == null) {
         return "unknown";
      } else {
         String serverBrand = clientPacketListener.serverBrand();
         return serverBrand != null && !serverBrand.isBlank() ? serverBrand : "unknown";
      }
   }

   public static boolean hasServerBrand(String expectedBrand) {
      for (String serverBrand : getServerBrand().split(",")) {
         if (serverBrand.trim().equalsIgnoreCase(expectedBrand)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public static ClientPacketListener getConnectedClientPacketListener() {
      Minecraft minecraft = Minecraft.getInstance();
      ClientPacketListener clientPacketListener = minecraft.getConnection();
      return clientPacketListener != null && clientPacketListener.getConnection().isConnected() ? clientPacketListener : null;
   }
}
