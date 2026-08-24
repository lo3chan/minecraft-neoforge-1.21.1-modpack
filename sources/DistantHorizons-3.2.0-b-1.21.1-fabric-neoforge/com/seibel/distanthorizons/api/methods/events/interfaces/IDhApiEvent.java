package com.seibel.distanthorizons.api.methods.events.interfaces;

import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IDhApiEvent<T> extends IBindable {
   default boolean removeAfterFiring() {
      return false;
   }

   void fireEvent(DhApiEventParam<T> dhApiEventParam);
}
