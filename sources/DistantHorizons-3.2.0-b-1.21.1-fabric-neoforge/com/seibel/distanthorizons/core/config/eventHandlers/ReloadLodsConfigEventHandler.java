package com.seibel.distanthorizons.core.config.eventHandlers;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderProxy;

public class ReloadLodsConfigEventHandler extends AbstractDelayedConfigEventHandler {
   public static ReloadLodsConfigEventHandler DELAYED_INSTANCE = new ReloadLodsConfigEventHandler(2000L);
   public static ReloadLodsConfigEventHandler INSTANT_INSTANCE = new ReloadLodsConfigEventHandler(0L);

   public ReloadLodsConfigEventHandler(long timeoutInMs) {
      super(timeoutInMs);
   }

   @Override
   public void onConfigTimeout() {
      IDhApiRenderProxy renderProxy = DhApi.Delayed.renderProxy;
      if (renderProxy != null) {
         renderProxy.clearRenderDataCache();
      }
   }
}
