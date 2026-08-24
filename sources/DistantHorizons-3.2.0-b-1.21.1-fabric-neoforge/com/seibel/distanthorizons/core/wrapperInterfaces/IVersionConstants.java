package com.seibel.distanthorizons.core.wrapperInterfaces;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingEngine;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IVersionConstants extends IBindable {
   String getMinecraftVersion();

   EDhApiRenderingEngine getDefaultRenderingEngine();
}
