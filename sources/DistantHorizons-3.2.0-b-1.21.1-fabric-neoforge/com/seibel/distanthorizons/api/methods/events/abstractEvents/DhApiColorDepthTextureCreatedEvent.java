package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiTextureCreatedParam;

@Deprecated
public abstract class DhApiColorDepthTextureCreatedEvent implements IDhApiEvent<DhApiColorDepthTextureCreatedEvent.EventParam> {
   public abstract void onResize(DhApiEventParam<DhApiColorDepthTextureCreatedEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiColorDepthTextureCreatedEvent.EventParam> event) {
      this.onResize(event);
   }

   public static class EventParam implements IDhApiEventParam {
      public final int previousWidth;
      public final int previousHeight;
      public final int newWidth;
      public final int newHeight;

      public EventParam(int previousWidth, int previousHeight, int newWidth, int newHeight) {
         this.previousWidth = previousWidth;
         this.previousHeight = previousHeight;
         this.newWidth = newWidth;
         this.newHeight = newHeight;
      }

      public EventParam(DhApiTextureCreatedParam textureCreatedParam) {
         this.previousWidth = textureCreatedParam.previousWidth;
         this.previousHeight = textureCreatedParam.previousHeight;
         this.newWidth = textureCreatedParam.newWidth;
         this.newHeight = textureCreatedParam.newHeight;
      }

      public DhApiColorDepthTextureCreatedEvent.EventParam copy() {
         return new DhApiColorDepthTextureCreatedEvent.EventParam(this.previousWidth, this.previousHeight, this.newWidth, this.newHeight);
      }
   }
}
