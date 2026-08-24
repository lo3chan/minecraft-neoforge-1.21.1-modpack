package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiTextureCreatedParam;

public abstract class DhApiAfterColorDepthTextureCreatedEvent implements IDhApiEvent<DhApiTextureCreatedParam> {
   public abstract void onResize(DhApiEventParam<DhApiTextureCreatedParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiTextureCreatedParam> event) {
      this.onResize(event);
   }
}
