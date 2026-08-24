package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IKeyedClientLevelManager extends IBindable {
   IServerKeyedClientLevel getServerKeyedLevel(IClientLevelWrapper iClientLevelWrapper);

   IServerKeyedClientLevel setServerKeyedLevel(IClientLevelWrapper iClientLevelWrapper, String string, String string2, String string3);

   void clearKeyedLevel();

   boolean isEnabled();

   void disable();
}
