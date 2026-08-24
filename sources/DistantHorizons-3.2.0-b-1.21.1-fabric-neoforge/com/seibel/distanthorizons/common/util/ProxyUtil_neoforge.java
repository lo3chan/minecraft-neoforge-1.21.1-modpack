package com.seibel.distanthorizons.common.util;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class ProxyUtil_neoforge {
   public static ILevelWrapper getLevelWrapper(LevelAccessor level) {
      ILevelWrapper levelWrapper;
      if (level instanceof ServerLevel) {
         levelWrapper = ServerLevelWrapper_neoforge.getWrapper((ServerLevel)level);
      } else {
         levelWrapper = ClientLevelWrapper_neoforge.getWrapper((ClientLevel)level);
      }

      return levelWrapper;
   }
}
