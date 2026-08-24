package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiBlockStateWrapperCreatedEvent implements IDhApiEvent<DhApiBlockStateWrapperCreatedEvent.EventParam> {
   public abstract void blockStateWrapperCreated(DhApiEventParam<DhApiBlockStateWrapperCreatedEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiBlockStateWrapperCreatedEvent.EventParam> event) {
      this.blockStateWrapperCreated(event);
   }

   public static class EventParam implements IDhApiEventParam {
      private final IDhApiBlockStateWrapper blockStateWrapper;
      private boolean overridesSet = false;
      private EDhApiBlockMaterial blockMaterial = null;
      private Integer opacity = null;
      private Boolean allowApiColorOverride = null;

      public EventParam(IDhApiBlockStateWrapper blockStateWrapper) {
         this.blockStateWrapper = blockStateWrapper;
      }

      public IDhApiBlockStateWrapper getBlockStateWrapper() {
         return this.blockStateWrapper;
      }

      public void setBlockMaterial(EDhApiBlockMaterial blockMaterial) {
         this.blockMaterial = blockMaterial;
         this.overridesSet = true;
      }

      public EDhApiBlockMaterial getBlockMaterial() {
         return this.blockMaterial;
      }

      public void setOpacity(int opacity) {
         this.opacity = opacity;
         this.overridesSet = true;
      }

      public Integer getOpacity() {
         return this.opacity;
      }

      public void setAllowApiColorOverride(boolean allowApiColorOverride) {
         this.allowApiColorOverride = allowApiColorOverride;
         this.overridesSet = true;
      }

      public Boolean getAllowApiColorOverride() {
         return this.allowApiColorOverride;
      }

      public boolean getOverridesSet() {
         return this.overridesSet;
      }

      public DhApiBlockStateWrapperCreatedEvent.EventParam copy() {
         return this;
      }

      @Override
      public boolean getCopyBeforeFire() {
         return false;
      }
   }
}
