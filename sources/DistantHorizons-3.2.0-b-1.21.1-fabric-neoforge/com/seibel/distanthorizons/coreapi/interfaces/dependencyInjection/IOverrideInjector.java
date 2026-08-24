package com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;

public interface IOverrideInjector<BindableType extends IBindable> {
   int CORE_PRIORITY = -1;
   int MIN_NON_CORE_OVERRIDE_PRIORITY = 0;
   int DEFAULT_NON_CORE_OVERRIDE_PRIORITY = 10;

   void bind(Class<? extends IDhApiOverrideable> class_, IDhApiOverrideable iDhApiOverrideable) throws IllegalStateException, IllegalArgumentException;

   <T extends IDhApiOverrideable> T get(Class<T> class_) throws ClassCastException;

   <T extends IDhApiOverrideable> T get(Class<T> class_, int i) throws ClassCastException;

   void unbind(Class<? extends IDhApiOverrideable> class_, IDhApiOverrideable iDhApiOverrideable);

   void clear();
}
