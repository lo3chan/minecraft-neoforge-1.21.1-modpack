package com.seibel.distanthorizons.core.config.api.converters;

import com.seibel.distanthorizons.coreapi.interfaces.config.IConverter;

public class InvertedBoolConverter implements IConverter<Boolean, Boolean> {
   public Boolean convertToCoreType(Boolean core) {
      return !core;
   }

   public Boolean convertToApiType(Boolean api) {
      return !api;
   }
}
