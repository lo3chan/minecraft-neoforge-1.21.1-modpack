package com.seibel.distanthorizons.common.wrappers.level;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import net.minecraft.class_638;

public class ServerKeyedClientLevelWrapper_fabric extends ClientLevelWrapper_fabric implements IServerKeyedClientLevel {
   private final String serverKey;
   private final String serverLevelKey;

   public ServerKeyedClientLevelWrapper_fabric(class_638 level, String serverKey, String serverLevelKey) {
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
