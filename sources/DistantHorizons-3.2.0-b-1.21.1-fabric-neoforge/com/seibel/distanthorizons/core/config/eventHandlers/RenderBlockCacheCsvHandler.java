package com.seibel.distanthorizons.core.config.eventHandlers;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;

public class RenderBlockCacheCsvHandler extends AbstractDelayedConfigEventHandler {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static RenderBlockCacheCsvHandler INSTANCE = new RenderBlockCacheCsvHandler();

   private RenderBlockCacheCsvHandler() {
      super(2000L);
   }

   @Override
   public void onConfigTimeout() {
      IWrapperFactory wrapperFactory = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
      if (wrapperFactory != null) {
         wrapperFactory.resetCachedIgnoredBlocksSets();
         DhApi.Delayed.renderProxy.clearRenderDataCache();
      }
   }
}
