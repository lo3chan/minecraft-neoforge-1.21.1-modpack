package com.seibel.distanthorizons.core.config.api.converters;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiRendererMode;
import com.seibel.distanthorizons.coreapi.interfaces.config.IConverter;

public class RenderModeEnabledConverter implements IConverter<EDhApiRendererMode, Boolean> {
   public EDhApiRendererMode convertToCoreType(Boolean renderingEnabled) {
      return renderingEnabled ? EDhApiRendererMode.DEFAULT : EDhApiRendererMode.DISABLED;
   }

   public Boolean convertToApiType(EDhApiRendererMode renderingMode) {
      return renderingMode == EDhApiRendererMode.DEFAULT;
   }
}
