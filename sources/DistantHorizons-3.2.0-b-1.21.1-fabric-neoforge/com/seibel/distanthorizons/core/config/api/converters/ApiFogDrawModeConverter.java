package com.seibel.distanthorizons.core.config.api.converters;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogDrawMode;
import com.seibel.distanthorizons.coreapi.interfaces.config.IConverter;

@Deprecated
public class ApiFogDrawModeConverter implements IConverter<Boolean, EDhApiFogDrawMode> {
   public Boolean convertToCoreType(EDhApiFogDrawMode renderingMode) {
      return renderingMode == EDhApiFogDrawMode.USE_OPTIFINE_SETTING ? true : renderingMode == EDhApiFogDrawMode.FOG_ENABLED;
   }

   public EDhApiFogDrawMode convertToApiType(Boolean renderingEnabled) {
      return renderingEnabled ? EDhApiFogDrawMode.FOG_ENABLED : EDhApiFogDrawMode.FOG_DISABLED;
   }
}
