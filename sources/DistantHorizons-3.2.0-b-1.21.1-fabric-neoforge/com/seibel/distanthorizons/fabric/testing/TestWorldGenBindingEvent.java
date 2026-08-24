package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import net.minecraft.class_3218;

public class TestWorldGenBindingEvent extends DhApiLevelLoadEvent {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   @Override
   public void onLevelLoad(DhApiEventParam<DhApiLevelLoadEvent.EventParam> event) {
      LOGGER.info("DH Level: [" + event.value.levelWrapper.getDimensionType() + "] loaded.");

      try {
         class_3218 level = (class_3218)event.value.levelWrapper.getWrappedMcObject();
         IDhApiWorldGenerator exampleWorldGen = new TestGenericWorldGenerator(event.value.levelWrapper);
         DhApi.worldGenOverrides.registerWorldGeneratorOverride(event.value.levelWrapper, exampleWorldGen);
      } catch (ClassCastException var4) {
         LOGGER.warn(
            "Unable to add world generator to level wrapper ["
               + event.value.levelWrapper.getClass()
               + "] - ["
               + event.value.levelWrapper.getDimensionType()
               + "]."
         );
      }
   }
}
