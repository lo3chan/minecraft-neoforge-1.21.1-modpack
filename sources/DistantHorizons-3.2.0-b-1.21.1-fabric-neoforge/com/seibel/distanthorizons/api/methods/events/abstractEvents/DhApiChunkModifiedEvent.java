package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiChunkModifiedEvent implements IDhApiEvent<DhApiChunkModifiedEvent.EventParam> {
   public abstract void onChunkModified(DhApiEventParam<DhApiChunkModifiedEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiChunkModifiedEvent.EventParam> input) {
      this.onChunkModified(input);
   }

   public static class EventParam implements IDhApiEventParam {
      public final IDhApiLevelWrapper levelWrapper;
      public final int chunkX;
      public final int chunkZ;

      public EventParam(IDhApiLevelWrapper newLevelWrapper, int chunkX, int chunkZ) {
         this.levelWrapper = newLevelWrapper;
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
      }

      public DhApiChunkModifiedEvent.EventParam copy() {
         return new DhApiChunkModifiedEvent.EventParam(this.levelWrapper, this.chunkX, this.chunkZ);
      }
   }
}
