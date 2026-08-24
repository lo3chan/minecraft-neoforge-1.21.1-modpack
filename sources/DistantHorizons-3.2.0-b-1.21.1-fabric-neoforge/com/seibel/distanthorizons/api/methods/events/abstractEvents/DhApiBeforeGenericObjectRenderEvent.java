package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiCancelableEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;

public abstract class DhApiBeforeGenericObjectRenderEvent implements IDhApiCancelableEvent<DhApiBeforeGenericObjectRenderEvent.EventParam> {
   public abstract void beforeRender(DhApiCancelableEventParam<DhApiBeforeGenericObjectRenderEvent.EventParam> dhApiCancelableEventParam);

   @Override
   public final void fireEvent(DhApiCancelableEventParam<DhApiBeforeGenericObjectRenderEvent.EventParam> input) {
      this.beforeRender(input);
   }

   public static class EventParam extends DhApiRenderParam implements IDhApiEventParam {
      public long boxGroupId;
      public String resourceLocationNamespace;
      public String resourceLocationPath;

      public void update(DhApiRenderParam renderParam, IDhApiRenderableBoxGroup boxGroup) {
         super.update(renderParam);
         this.boxGroupId = boxGroup.getId();
         this.resourceLocationNamespace = boxGroup.getResourceLocationNamespace();
         this.resourceLocationPath = boxGroup.getResourceLocationPath();
      }

      public DhApiBeforeGenericObjectRenderEvent.EventParam copy() {
         return this;
      }
   }
}
