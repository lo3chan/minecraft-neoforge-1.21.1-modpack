package com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor;

import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.io.File;

public interface IModChecker extends IBindable {
   boolean isModLoaded(String string);

   File modLocation(String string);
}
