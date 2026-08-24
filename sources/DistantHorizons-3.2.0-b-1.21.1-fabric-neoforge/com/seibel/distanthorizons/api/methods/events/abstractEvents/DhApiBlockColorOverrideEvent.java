package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;

public abstract class DhApiBlockColorOverrideEvent implements IDhApiEvent<DhApiBlockColorOverrideEvent.EventParam> {
   public abstract void onBlockColorOverridden(DhApiEventParam<DhApiBlockColorOverrideEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiBlockColorOverrideEvent.EventParam> event) {
      this.onBlockColorOverridden(event);
   }

   public static class EventParam implements IDhApiEventParam {
      private IDhApiLevelWrapper levelWrapper;
      private IDhApiFullDataSource dataSource;
      private IDhApiBlockStateWrapper blockStateWrapper = null;
      private IDhApiBiomeWrapper biomeWrapper = null;
      private int colorAsInt = -1;
      private int blockPosX = 0;
      private int blockPosY = 0;
      private int blockPosZ = 0;

      public void update(
         IDhApiLevelWrapper levelWrapper,
         IDhApiFullDataSource dataSource,
         IDhApiBlockStateWrapper blockStateWrapper,
         IDhApiBiomeWrapper biomeWrapper,
         int colorAsInt,
         int blockPosX,
         int blockPosY,
         int blockPosZ
      ) {
         this.levelWrapper = levelWrapper;
         this.dataSource = dataSource;
         this.blockStateWrapper = blockStateWrapper;
         this.biomeWrapper = biomeWrapper;
         this.colorAsInt = colorAsInt;
         this.blockPosX = blockPosX;
         this.blockPosY = blockPosY;
         this.blockPosZ = blockPosZ;
      }

      public IDhApiBlockStateWrapper getBlockStateWrapper() {
         return this.blockStateWrapper;
      }

      public IDhApiBiomeWrapper getBiomeWrapper() {
         return this.biomeWrapper;
      }

      public IDhApiLevelWrapper getLevelWrapper() {
         return this.levelWrapper;
      }

      public IDhApiFullDataSource getDataSource() {
         return this.dataSource;
      }

      public int getColorAsInt() {
         return this.colorAsInt;
      }

      public int getAlpha() {
         return ColorUtil.getAlpha(this.colorAsInt);
      }

      public int getRed() {
         return ColorUtil.getRed(this.colorAsInt);
      }

      public int getGreen() {
         return ColorUtil.getGreen(this.colorAsInt);
      }

      public int getBlue() {
         return ColorUtil.getBlue(this.colorAsInt);
      }

      public void setColor(int red, int green, int blue) throws IllegalArgumentException {
         this.setColor(this.getAlpha(), red, green, blue);
      }

      public void setColor(int alpha, int red, int green, int blue) throws IllegalArgumentException {
         ColorUtil.throwIfColorValueOutOfIntRange("alpha", alpha);
         ColorUtil.throwIfColorValueOutOfIntRange("red", red);
         ColorUtil.throwIfColorValueOutOfIntRange("green", green);
         ColorUtil.throwIfColorValueOutOfIntRange("blue", blue);
         this.colorAsInt = ColorUtil.argbToInt(alpha, red, green, blue);
      }

      public int getBlockPosX() {
         return this.blockPosX;
      }

      public int getBlockPosY() {
         return this.blockPosY;
      }

      public int getBlockPosZ() {
         return this.blockPosZ;
      }

      public DhApiBlockColorOverrideEvent.EventParam copy() {
         return this;
      }

      @Override
      public boolean getCopyBeforeFire() {
         return false;
      }
   }
}
