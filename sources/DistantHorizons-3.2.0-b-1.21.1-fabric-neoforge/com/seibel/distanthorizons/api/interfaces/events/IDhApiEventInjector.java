package com.seibel.distanthorizons.api.interfaces.events;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IDependencyInjector;

public interface IDhApiEventInjector extends IDependencyInjector<IDhApiEvent> {
   boolean unbind(Class<? extends IDhApiEvent> class_, Class<? extends IDhApiEvent> class2) throws IllegalArgumentException;

   <T, U extends IDhApiEvent<T>> boolean fireAllEvents(Class<U> class_, T object);
}
