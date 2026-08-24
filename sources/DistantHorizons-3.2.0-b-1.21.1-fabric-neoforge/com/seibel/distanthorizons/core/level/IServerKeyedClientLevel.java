package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;

public interface IServerKeyedClientLevel extends IClientLevelWrapper {
   String getServerKey();

   String getServerLevelKey();
}
