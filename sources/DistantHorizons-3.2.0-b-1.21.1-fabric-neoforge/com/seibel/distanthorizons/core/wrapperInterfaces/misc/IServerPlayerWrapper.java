package com.seibel.distanthorizons.core.wrapperInterfaces.misc;

import com.seibel.distanthorizons.api.interfaces.IDhApiUnsafeWrapper;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;

public interface IServerPlayerWrapper extends IDhApiUnsafeWrapper {
   String getName();

   IServerLevelWrapper getLevel();

   DhVec3d getPosition();
}
