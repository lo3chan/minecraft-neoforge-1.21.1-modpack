package com.seibel.distanthorizons.common.wrappers.level;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import net.minecraft.client.multiplayer.ClientLevel;

public class ServerKeyedClientLevelWrapper_neoforge extends ClientLevelWrapper_neoforge implements IServerKeyedClientLevel {
   private final String serverKey;
   private final String serverLevelKey;

   public ServerKeyedClientLevelWrapper_neoforge(ClientLevel level, String serverKey, String serverLevelKey) {
      super(level);
      this.serverKey = serverKey;
      this.serverLevelKey = serverLevelKey;
   }

   @Override
   public String getServerKey() {
      return this.serverKey;
   }

   @Override
   public String getServerLevelKey() {
      return this.serverLevelKey;
   }

   @Override
   public String getDhIdentifier() {
      return this.getServerLevelKey();
   }
}
