package com.seibel.distanthorizons.core.wrapperInterfaces.config;

import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface ILangWrapper extends IBindable {
   boolean langExists(String string);

   String getLang(String string);
}
