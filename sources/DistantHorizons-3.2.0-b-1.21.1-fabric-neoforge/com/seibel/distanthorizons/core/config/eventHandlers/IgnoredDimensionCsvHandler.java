package com.seibel.distanthorizons.core.config.eventHandlers;

import com.seibel.distanthorizons.api.enums.config.EDhApiMcRenderingFadeMode;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.util.StringUtil;

public class IgnoredDimensionCsvHandler extends DhApiBeforeRenderEvent implements IConfigListener {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static IgnoredDimensionCsvHandler INSTANCE = new IgnoredDimensionCsvHandler();
   private String[] dimensionNames = null;

   private IgnoredDimensionCsvHandler() {
   }

   @Override
   public void onConfigValueSet() {
      String ignoredDimensionCsvString = Config.Client.Advanced.Graphics.Experimental.ignoredDimensionCsv.get();
      if (ignoredDimensionCsvString != null && !ignoredDimensionCsvString.isEmpty()) {
         try {
            this.dimensionNames = ignoredDimensionCsvString.split(",");
            LOGGER.info("DH set to ignore dimensions: [" + StringUtil.join(", ", this.dimensionNames) + "].");
         } catch (Exception var3) {
            LOGGER.error("Failed to separate ignored dimensions from CSV string, error: [" + var3.getMessage() + "].", var3);
            this.dimensionNames = null;
         }
      } else {
         LOGGER.info("Dimension ignoring disabled, DH will render all dimensions.");
         this.dimensionNames = null;
      }
   }

   @Override
   public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
      String dimName = event.value.clientLevelWrapper.getDimensionName();
      if (INSTANCE.dimensionNameShouldBeIgnored(dimName)) {
         event.cancelEvent();
         Config.Client.Advanced.Graphics.Fog.enableVanillaFog.setApiValue(true);
         Config.Client.Advanced.Graphics.Quality.vanillaFadeMode.setApiValue(EDhApiMcRenderingFadeMode.NONE);
      } else {
         Config.Client.Advanced.Graphics.Fog.enableVanillaFog.setApiValue(null);
         Config.Client.Advanced.Graphics.Quality.vanillaFadeMode.setApiValue(null);
      }
   }

   public boolean dimensionNameShouldBeIgnored(String dimName) {
      if (this.dimensionNames != null && this.dimensionNames.length != 0) {
         int atIndex = dimName.indexOf(64);
         if (atIndex >= 0) {
            dimName = dimName.substring(atIndex + 1);
         }

         for (int i = 0; i < this.dimensionNames.length; i++) {
            String dimNameToIgnore = this.dimensionNames[i];
            if (dimName.equalsIgnoreCase(dimNameToIgnore)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
