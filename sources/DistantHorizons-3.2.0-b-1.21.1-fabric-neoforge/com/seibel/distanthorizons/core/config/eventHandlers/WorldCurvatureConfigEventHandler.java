package com.seibel.distanthorizons.core.config.eventHandlers;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;

public class WorldCurvatureConfigEventHandler implements IConfigListener {
   public static WorldCurvatureConfigEventHandler INSTANCE = new WorldCurvatureConfigEventHandler();
   public static final int MIN_VALID_CURVE_VALUE = 50;

   private WorldCurvatureConfigEventHandler() {
   }

   @Override
   public void onConfigValueSet() {
      int curveRatio = Config.Client.Advanced.Graphics.Experimental.earthCurveRatio.get();
      if (curveRatio > 0 && curveRatio < 50) {
         Config.Client.Advanced.Graphics.Experimental.earthCurveRatio.set(50);
      } else if (curveRatio < 0 && curveRatio > -50) {
         Config.Client.Advanced.Graphics.Experimental.earthCurveRatio.set(-50);
      }
   }

   @Override
   public void onUiModify() {
   }
}
