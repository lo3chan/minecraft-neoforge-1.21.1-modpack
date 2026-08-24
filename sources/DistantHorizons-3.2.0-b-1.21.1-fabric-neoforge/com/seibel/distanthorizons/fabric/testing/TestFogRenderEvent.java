package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeFogRenderEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiFogRenderParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiMutableFogRenderParam;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;

public class TestFogRenderEvent extends DhApiBeforeFogRenderEvent {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final float INCREMENT_PER_SECOND = 0.25F;
   private float lastFar = 1.0F;
   private long lastUpdateTime = System.nanoTime();

   @Override
   public void beforeRender(DhApiCancelableEventParam<DhApiBeforeFogRenderEvent.EventParam> event) {
      DhApiBeforeFogRenderEvent.EventParam eventParam = event.value;
      DhApiFogRenderParam originalParms = eventParam.getOriginalFogRenderParam();
      DhApiMutableFogRenderParam mutableParms = eventParam.getFogRenderParam();
      long currentTime = System.nanoTime();
      float deltaSeconds = (float)(currentTime - this.lastUpdateTime) / 1.0E9F;
      this.lastUpdateTime = currentTime;
      this.lastFar += 0.25F * deltaSeconds;
      if (this.lastFar >= originalParms.getFarFogEndPercent()) {
         this.lastFar = 0.0F;
      }

      mutableParms.setFarFogStartPercent(this.lastFar);
      mutableParms.setFarFogEndPercent(this.lastFar + 0.5F);
   }
}
