package com.seibel.distanthorizons.core.wrapperInterfaces.world;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IBiomeWrapper extends IDhApiBiomeWrapper, IBindable {
   @Override
   String getName();

   String getSerialString();
}
