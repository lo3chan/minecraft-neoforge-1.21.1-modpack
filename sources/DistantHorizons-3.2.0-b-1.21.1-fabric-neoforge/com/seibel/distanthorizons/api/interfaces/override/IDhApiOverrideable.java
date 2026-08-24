package com.seibel.distanthorizons.api.interfaces.override;

import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IDhApiOverrideable extends IBindable {
   default int getPriority() {
      return 10;
   }
}
