package com.seibel.distanthorizons.coreapi.interfaces.config;

public interface IConverter<CoreType, ApiType> {
   CoreType convertToCoreType(ApiType object);

   ApiType convertToApiType(CoreType object);
}
