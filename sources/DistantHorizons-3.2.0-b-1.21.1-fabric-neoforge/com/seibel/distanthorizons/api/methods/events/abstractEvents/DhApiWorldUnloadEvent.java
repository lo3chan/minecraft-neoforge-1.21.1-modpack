package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiWorldUnloadEvent implements IDhApiEvent<DhApiWorldUnloadEvent.EventParam> {
   public abstract void onWorldUnload(DhApiEventParam<DhApiWorldUnloadEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiWorldUnloadEvent.EventParam> input) {
      this.onWorldUnload(input);
   }

   public static class EventParam implements IDhApiEventParam {
      public DhApiWorldLoadEvent.EventParam copy() {
         return new DhApiWorldLoadEvent.EventParam();
      }
   }
}
