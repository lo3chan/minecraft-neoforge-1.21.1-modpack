package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockColorOverrideEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import java.awt.Color;
import java.io.IOException;

public class TestCustomColorEvent extends DhApiBlockColorOverrideEvent {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   @Override
   public void onBlockColorOverridden(DhApiEventParam<DhApiBlockColorOverrideEvent.EventParam> event) {
      DhApiBlockColorOverrideEvent.EventParam eventParam = event.value;
      this.useWaterTint(eventParam);
   }

   private void randomDatapointColors(DhApiBlockColorOverrideEvent.EventParam eventParam) {
      int a = eventParam.getAlpha();
      int r = eventParam.getRed();
      int g = eventParam.getGreen();
      int b = eventParam.getBlue();
      if (eventParam.getBlockStateWrapper().getOpacity() == 16) {
         eventParam.setColor(255, r, g, b);
      } else {
         eventParam.setColor(60, r, g, b);
      }
   }

   private void randomPerBlockColors(DhApiBlockColorOverrideEvent.EventParam eventParam) {
      int r = Math.abs(eventParam.getBlockStateWrapper().hashCode() % 255);
      int g = Math.abs((eventParam.getBlockStateWrapper().hashCode() << 4) % 255);
      int b = Math.abs((eventParam.getBlockStateWrapper().hashCode() << 8) % 255);
      eventParam.setColor(r, g, b);
   }

   private void useWaterTint(DhApiBlockColorOverrideEvent.EventParam eventParam) {
      IDhApiBlockStateWrapper blockWrapper;
      try {
         String blockNamespace = "minecraft:water";
         blockWrapper = DhApi.Delayed.wrapperFactory.getDefaultBlockStateWrapper(blockNamespace, eventParam.getLevelWrapper());
      } catch (IOException var4) {
         blockWrapper = eventParam.getBlockStateWrapper();
      }

      DhApiResult<Color> result = eventParam.getLevelWrapper()
         .getBlockColorPreApi(
            blockWrapper,
            eventParam.getBiomeWrapper(),
            eventParam.getBlockPosX(),
            eventParam.getBlockPosY(),
            eventParam.getBlockPosZ(),
            eventParam.getDataSource()
         );
      if (result.success) {
         eventParam.setColor(result.payload.getRed(), result.payload.getGreen(), result.payload.getBlue());
      }
   }

   private void blackWhitePositionStripe(DhApiBlockColorOverrideEvent.EventParam eventParam) {
      int r = Math.abs(eventParam.getBlockPosX() % 255);
      eventParam.setColor(r, r, r);
   }

   private void positionRainbow(DhApiBlockColorOverrideEvent.EventParam eventParam) {
      float[] ahsv = ColorUtil.argbToAhsv(ColorUtil.RED);
      float a = ahsv[0];
      int xModPos = Math.abs(eventParam.getBlockPosX() % 510);
      float h = xModPos < 255 ? xModPos : 510 - xModPos;
      float s = ahsv[2];
      float v = ahsv[3];
      int colorInt = ColorUtil.ahsvToArgb(a, h, s, v);
      eventParam.setColor(ColorUtil.getRed(colorInt), ColorUtil.getGreen(colorInt), ColorUtil.getBlue(colorInt));
   }
}
