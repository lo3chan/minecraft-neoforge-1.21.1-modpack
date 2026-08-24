package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.IOException;

public class TestChunkInputReplacerEvent extends DhApiChunkProcessingEvent {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final String REPLACEMENT_BLOCK_STATE_NAMESPACE = "minecraft:stone";
   private IDhApiBlockStateWrapper stoneBlockWrapper = null;
   private boolean initialBlockSetupComplete = false;

   @Override
   public void blockOrBiomeChangedDuringChunkProcessing(DhApiEventParam<DhApiChunkProcessingEvent.EventParam> event) {
      if (!this.initialBlockSetupComplete) {
         synchronized (this) {
            this.initialBlockSetupComplete = true;

            try {
               this.stoneBlockWrapper = DhApi.Delayed.wrapperFactory.getDefaultBlockStateWrapper("minecraft:stone", event.value.levelWrapper);
            } catch (IOException var5) {
               LOGGER.error("Unable to get [minecraft:stone] block replacement cannot continue and is now disabled, error: [" + var5.getMessage() + "].", var5);
               DhApi.events.unbind(DhApiChunkProcessingEvent.class, (Class<? extends IDhApiEvent>)this.getClass());
            }
         }
      }

      if (this.stoneBlockWrapper != null) {
         IDhApiBlockStateWrapper block = event.value.currentBlock;
         if (block.getSerialString().contains("grass_block") || block.getSerialString().contains("dirt")) {
            event.value.setBlockOverride(this.stoneBlockWrapper);
         }
      }
   }
}
