package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiLevelLoadEvent implements IDhApiEvent<DhApiLevelLoadEvent.EventParam> {
   public abstract void onLevelLoad(DhApiEventParam<DhApiLevelLoadEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiLevelLoadEvent.EventParam> input) {
      this.onLevelLoad(input);
   }

   public static class EventParam implements IDhApiEventParam {
      public final IDhApiLevelWrapper levelWrapper;

      public EventParam(IDhApiLevelWrapper newLevelWrapper) {
         this.levelWrapper = newLevelWrapper;
      }

      public DhApiLevelLoadEvent.EventParam copy() {
         return new DhApiLevelLoadEvent.EventParam(this.levelWrapper);
      }
   }
}
