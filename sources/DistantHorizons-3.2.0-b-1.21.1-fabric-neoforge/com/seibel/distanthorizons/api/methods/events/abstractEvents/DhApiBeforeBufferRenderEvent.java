package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3f;

public abstract class DhApiBeforeBufferRenderEvent implements IDhApiEvent<DhApiBeforeBufferRenderEvent.EventParam> {
   public abstract void beforeRender(DhApiEventParam<DhApiBeforeBufferRenderEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiBeforeBufferRenderEvent.EventParam> input) {
      this.beforeRender(input);
   }

   public static class EventParam extends DhApiRenderParam implements IDhApiEventParam {
      public DhApiVec3f modelPos;

      public void update(DhApiRenderParam parent, DhApiVec3f modelPos) {
         super.update(parent);
         this.modelPos = modelPos;
      }

      @Override
      public boolean getCopyBeforeFire() {
         return false;
      }

      public DhApiBeforeBufferRenderEvent.EventParam copy() {
         return this;
      }
   }
}
