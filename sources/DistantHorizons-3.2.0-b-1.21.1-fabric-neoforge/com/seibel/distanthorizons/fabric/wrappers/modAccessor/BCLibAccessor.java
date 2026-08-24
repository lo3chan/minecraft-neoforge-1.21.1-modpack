package com.seibel.distanthorizons.fabric.wrappers.modAccessor;

import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IBCLibAccessor;

public class BCLibAccessor implements IBCLibAccessor {
   @Override
   public String getModName() {
      return "BCLib";
   }

   @Override
   public void setRenderCustomFog(boolean newValue) {
   }
}
