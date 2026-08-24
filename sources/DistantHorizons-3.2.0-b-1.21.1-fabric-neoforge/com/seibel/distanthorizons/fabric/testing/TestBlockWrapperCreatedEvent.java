package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockStateWrapperCreatedEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;

public class TestBlockWrapperCreatedEvent extends DhApiBlockStateWrapperCreatedEvent {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   @Override
   public void blockStateWrapperCreated(DhApiEventParam<DhApiBlockStateWrapperCreatedEvent.EventParam> event) {
      DhApiBlockStateWrapperCreatedEvent.EventParam eventParam = event.value;
      eventParam.setAllowApiColorOverride(true);
   }
}
