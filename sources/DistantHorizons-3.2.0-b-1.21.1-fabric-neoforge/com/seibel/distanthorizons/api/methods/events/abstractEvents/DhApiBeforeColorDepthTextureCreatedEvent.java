package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiTextureCreatedParam;

@Deprecated
public abstract class DhApiBeforeColorDepthTextureCreatedEvent implements IDhApiEvent<DhApiTextureCreatedParam> {
   public abstract void onResize(DhApiEventParam<DhApiTextureCreatedParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiTextureCreatedParam> event) {
      this.onResize(event);
   }
}
