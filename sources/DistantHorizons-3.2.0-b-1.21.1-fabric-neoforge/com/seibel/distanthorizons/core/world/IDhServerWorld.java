package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerStateManager;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import org.jetbrains.annotations.Nullable;

public interface IDhServerWorld extends IDhWorld {
   ServerPlayerStateManager getServerPlayerStateManager();

   void addPlayer(IServerPlayerWrapper iServerPlayerWrapper);

   void removePlayer(IServerPlayerWrapper iServerPlayerWrapper);

   void changePlayerLevel(IServerPlayerWrapper iServerPlayerWrapper, IServerLevelWrapper iServerLevelWrapper, IServerLevelWrapper iServerLevelWrapper2);

   @Nullable
   default IDhServerLevel getOrLoadServerLevel(ILevelWrapper levelWrapper) {
      return (IDhServerLevel)this.getOrLoadLevel(levelWrapper);
   }
}
