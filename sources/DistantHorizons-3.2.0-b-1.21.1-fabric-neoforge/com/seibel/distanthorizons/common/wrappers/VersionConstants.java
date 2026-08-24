package com.seibel.distanthorizons.common.wrappers;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingEngine;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;

public class VersionConstants implements IVersionConstants {
   public static final VersionConstants INSTANCE = new VersionConstants();

   private VersionConstants() {
   }

   @Override
   public String getMinecraftVersion() {
      return "1.21.1";
   }

   @Override
   public EDhApiRenderingEngine getDefaultRenderingEngine() {
      return EDhApiRenderingEngine.OPEN_GL;
   }
}
