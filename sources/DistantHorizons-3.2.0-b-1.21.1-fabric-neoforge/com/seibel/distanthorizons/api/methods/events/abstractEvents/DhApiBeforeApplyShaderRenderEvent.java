package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiCancelableEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;

public abstract class DhApiBeforeApplyShaderRenderEvent implements IDhApiCancelableEvent<DhApiRenderParam> {
   public abstract void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> dhApiCancelableEventParam);

   @Override
   public final void fireEvent(DhApiCancelableEventParam<DhApiRenderParam> event) {
      this.beforeRender(event);
   }
}
