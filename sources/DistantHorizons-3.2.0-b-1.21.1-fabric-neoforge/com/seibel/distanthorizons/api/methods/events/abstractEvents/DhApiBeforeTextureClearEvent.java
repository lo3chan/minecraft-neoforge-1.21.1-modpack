package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiCancelableEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;

public abstract class DhApiBeforeTextureClearEvent implements IDhApiCancelableEvent<DhApiRenderParam> {
   public abstract void beforeClear(DhApiCancelableEventParam<DhApiRenderParam> dhApiCancelableEventParam);

   @Override
   public final void fireEvent(DhApiCancelableEventParam<DhApiRenderParam> input) {
      this.beforeClear(input);
   }
}
