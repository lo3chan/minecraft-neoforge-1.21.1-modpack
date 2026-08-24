package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiLevelUnloadEvent implements IDhApiEvent<DhApiLevelUnloadEvent.EventParam> {
   public abstract void onLevelUnload(DhApiEventParam<DhApiLevelUnloadEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiLevelUnloadEvent.EventParam> input) {
      this.onLevelUnload(input);
   }

   public static class EventParam implements IDhApiEventParam {
      public final IDhApiLevelWrapper levelWrapper;

      public EventParam(IDhApiLevelWrapper newLevelWrapper) {
         this.levelWrapper = newLevelWrapper;
      }

      public DhApiLevelUnloadEvent.EventParam copy() {
         return new DhApiLevelUnloadEvent.EventParam(this.levelWrapper);
      }
   }
}
