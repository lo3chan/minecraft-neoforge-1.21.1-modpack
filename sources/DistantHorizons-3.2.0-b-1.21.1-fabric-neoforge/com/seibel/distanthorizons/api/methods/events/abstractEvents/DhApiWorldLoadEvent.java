package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiWorldLoadEvent implements IDhApiEvent<DhApiWorldLoadEvent.EventParam> {
   public abstract void onWorldLoad(DhApiEventParam<DhApiWorldLoadEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiWorldLoadEvent.EventParam> input) {
      this.onWorldLoad(input);
   }

   public static class EventParam implements IDhApiEventParam {
      public DhApiWorldLoadEvent.EventParam copy() {
         return new DhApiWorldLoadEvent.EventParam();
      }
   }
}
