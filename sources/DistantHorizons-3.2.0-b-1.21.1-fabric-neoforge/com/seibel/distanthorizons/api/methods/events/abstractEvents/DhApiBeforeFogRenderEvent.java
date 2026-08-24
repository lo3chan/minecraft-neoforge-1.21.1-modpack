package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiCancelableEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiFogRenderParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiMutableFogRenderParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;

public abstract class DhApiBeforeFogRenderEvent implements IDhApiCancelableEvent<DhApiBeforeFogRenderEvent.EventParam> {
   public abstract void beforeRender(DhApiCancelableEventParam<DhApiBeforeFogRenderEvent.EventParam> dhApiCancelableEventParam);

   @Override
   public final void fireEvent(DhApiCancelableEventParam<DhApiBeforeFogRenderEvent.EventParam> event) {
      this.beforeRender(event);
   }

   public static class EventParam implements IDhApiEventParam {
      private DhApiRenderParam renderParam;
      private DhApiFogRenderParam originalFogRenderParam;
      private DhApiMutableFogRenderParam fogRenderParam;

      public void update(DhApiRenderParam renderParam, DhApiFogRenderParam fogRenderParam) {
         this.renderParam = renderParam;
         this.originalFogRenderParam = fogRenderParam;
         this.fogRenderParam = new DhApiMutableFogRenderParam(fogRenderParam);
      }

      public DhApiRenderParam getRenderParam() {
         return this.renderParam;
      }

      public DhApiFogRenderParam getOriginalFogRenderParam() {
         return this.originalFogRenderParam;
      }

      public DhApiMutableFogRenderParam getFogRenderParam() {
         return this.fogRenderParam;
      }

      public DhApiBeforeFogRenderEvent.EventParam copy() {
         return this;
      }

      @Override
      public boolean getCopyBeforeFire() {
         return false;
      }
   }
}
