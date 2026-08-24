package com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection;

import java.util.ArrayList;

public interface IDependencyInjector<BindableType extends IBindable> {
   void bind(Class<? extends BindableType> class_, BindableType iBindable) throws IllegalStateException, IllegalArgumentException;

   boolean checkIfClassImplements(Class<?> class_, Class<?> class2);

   boolean checkIfClassExtends(Class<?> class_, Class<?> class2);

   <T extends BindableType> T get(Class<T> class_) throws ClassCastException;

   <T extends BindableType> ArrayList<T> getAll(Class<T> class_) throws ClassCastException;

   <T extends BindableType> T get(Class<T> class_, boolean bl) throws ClassCastException;

   void clear();

   void runDelayedSetup();
}
