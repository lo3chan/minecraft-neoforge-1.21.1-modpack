package com.seibel.distanthorizons.core.network.messages;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;

public interface ILevelRelatedMessage {
   String getLevelName();

   default boolean isSameLevelAs(ILevelWrapper levelWrapper) {
      return levelWrapper instanceof IServerLevelWrapper
         ? this.getLevelName().equals(((IServerLevelWrapper)levelWrapper).getKeyedLevelDimensionName())
         : this.getLevelName().equals(levelWrapper.getDhIdentifier());
   }
}
