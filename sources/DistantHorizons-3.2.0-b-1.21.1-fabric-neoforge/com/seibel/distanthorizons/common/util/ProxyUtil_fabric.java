package com.seibel.distanthorizons.common.util;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import net.minecraft.class_1936;
import net.minecraft.class_3218;
import net.minecraft.class_638;

public class ProxyUtil_fabric {
   public static ILevelWrapper getLevelWrapper(class_1936 level) {
      ILevelWrapper levelWrapper;
      if (level instanceof class_3218) {
         levelWrapper = ServerLevelWrapper_fabric.getWrapper((class_3218)level);
      } else {
         levelWrapper = ClientLevelWrapper_fabric.getWrapper((class_638)level);
      }

      return levelWrapper;
   }
}
