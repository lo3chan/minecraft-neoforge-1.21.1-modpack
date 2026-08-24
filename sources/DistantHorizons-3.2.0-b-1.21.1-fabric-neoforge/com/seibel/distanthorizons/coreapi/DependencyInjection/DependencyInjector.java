package com.seibel.distanthorizons.coreapi.DependencyInjection;

import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IDependencyInjector;
import java.util.ArrayList;
import java.util.HashMap;

public class DependencyInjector<BindableType extends IBindable> implements IDependencyInjector<BindableType> {
   private static final ArrayList<?> EMPTY_GET_ALL_LIST = new ArrayList();
   protected final HashMap<Class<? extends BindableType>, ArrayList<BindableType>> dependencies = new HashMap<>();
   protected final Class<? extends BindableType> bindableInterface;
   protected final boolean allowDuplicateBindings;

   public DependencyInjector(Class<BindableType> newBindableInterface, boolean newAllowDuplicateBindings) {
      this.bindableInterface = newBindableInterface;
      this.allowDuplicateBindings = newAllowDuplicateBindings;
   }

   @Override
   public void bind(Class<? extends BindableType> dependencyInterface, BindableType dependencyImplementation) throws IllegalStateException, IllegalArgumentException {
      if (this.dependencies.containsKey(dependencyInterface) && !this.allowDuplicateBindings) {
         throw new IllegalStateException("The dependency [" + dependencyInterface.getSimpleName() + "] has already been bound.");
      } else if (dependencyImplementation == null) {
         throw new NullPointerException("Can't bind null to [" + dependencyInterface.getSimpleName() + "]");
      } else {
         boolean implementsInterface = this.checkIfClassImplements(dependencyImplementation.getClass(), dependencyInterface)
            || this.checkIfClassExtends(dependencyImplementation.getClass(), dependencyInterface);
         boolean implementsBindable = this.checkIfClassImplements(dependencyImplementation.getClass(), this.bindableInterface);
         if (!implementsInterface) {
            throw new IllegalArgumentException(
               "The dependency ["
                  + dependencyImplementation.getClass().getSimpleName()
                  + "] doesn't implement or extend: ["
                  + dependencyInterface.getSimpleName()
                  + "]."
            );
         } else if (!implementsBindable) {
            throw new IllegalArgumentException(
               "The dependency ["
                  + dependencyImplementation.getClass().getSimpleName()
                  + "] doesn't implement the interface: ["
                  + IBindable.class.getSimpleName()
                  + "]."
            );
         } else {
            if (!this.dependencies.containsKey(dependencyInterface)) {
               this.dependencies.put(dependencyInterface, new ArrayList<>());
            }

            this.dependencies.get(dependencyInterface).add(dependencyImplementation);
         }
      }
   }

   @Override
   public boolean checkIfClassImplements(Class<?> classToTest, Class<?> interfaceToLookFor) {
      if (classToTest.getSuperclass() != Object.class
         && classToTest.getSuperclass() != null
         && this.checkIfClassImplements(classToTest.getSuperclass(), interfaceToLookFor)) {
         return true;
      } else {
         for (Class<?> implementationInterface : classToTest.getInterfaces()) {
            if (implementationInterface.getInterfaces().length != 0 && this.checkIfClassImplements(implementationInterface, interfaceToLookFor)) {
               return true;
            }

            if (implementationInterface.equals(interfaceToLookFor)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean checkIfClassExtends(Class<?> classToTest, Class<?> extensionToLookFor) {
      return extensionToLookFor.isAssignableFrom(classToTest);
   }

   public void replaceBinding(Class<? extends BindableType> dependencyInterface, BindableType dependencyImplementation) throws IllegalStateException, IllegalArgumentException {
      this.unbindAll(dependencyInterface);
      this.bind(dependencyInterface, dependencyImplementation);
   }

   public void unbindAll(Class<? extends BindableType> dependencyInterface) throws IllegalStateException, IllegalArgumentException {
      this.dependencies.remove(dependencyInterface);
   }

   public void unbind(Class<? extends BindableType> dependencyInterface, BindableType dependencyImplementation) throws IllegalStateException, IllegalArgumentException {
      if (this.dependencies.containsKey(dependencyInterface)) {
         boolean implementsInterface = this.checkIfClassImplements(dependencyImplementation.getClass(), dependencyInterface)
            || this.checkIfClassExtends(dependencyImplementation.getClass(), dependencyInterface);
         boolean implementsBindable = this.checkIfClassImplements(dependencyImplementation.getClass(), this.bindableInterface);
         if (!implementsInterface) {
            throw new IllegalArgumentException(
               "The dependency ["
                  + dependencyImplementation.getClass().getSimpleName()
                  + "] doesn't implement or extend: ["
                  + dependencyInterface.getSimpleName()
                  + "]."
            );
         } else if (!implementsBindable) {
            throw new IllegalArgumentException(
               "The dependency ["
                  + dependencyImplementation.getClass().getSimpleName()
                  + "] doesn't implement the interface: ["
                  + IBindable.class.getSimpleName()
                  + "]."
            );
         } else {
            if (!this.dependencies.containsKey(dependencyInterface)) {
               this.dependencies.put(dependencyInterface, new ArrayList<>());
            }

            this.dependencies.get(dependencyInterface).remove(dependencyImplementation);
            this.dependencies.remove(dependencyInterface);
         }
      }
   }

   @Override
   public <T extends BindableType> T get(Class<T> interfaceClass) throws ClassCastException {
      return (T)((IBindable)this.getInternalLogic(interfaceClass, false).get(0));
   }

   @Override
   public <T extends BindableType> ArrayList<T> getAll(Class<T> interfaceClass) throws ClassCastException {
      return this.getInternalLogic(interfaceClass, false);
   }

   @Override
   public <T extends BindableType> T get(Class<T> interfaceClass, boolean allowIncompleteDependencies) throws ClassCastException {
      return (T)((IBindable)this.getInternalLogic(interfaceClass, allowIncompleteDependencies).get(0));
   }

   private <T extends BindableType> ArrayList<T> getInternalLogic(Class<T> interfaceClass, boolean allowIncompleteDependencies) throws ClassCastException {
      ArrayList<BindableType> dependencyList = this.dependencies.get(interfaceClass);
      if (dependencyList != null && dependencyList.size() != 0) {
         for (IBindable dependency : dependencyList) {
            if (!dependency.getDelayedSetupComplete() && !allowIncompleteDependencies) {
               throw new IllegalStateException(
                  "Got dependency of type [" + interfaceClass.getSimpleName() + "], but the dependency's delayed setup hasn't been run!"
               );
            }
         }

         return dependencyList;
      } else {
         return (ArrayList<T>)EMPTY_GET_ALL_LIST;
      }
   }

   @Override
   public void clear() {
      this.dependencies.clear();
   }

   @Override
   public void runDelayedSetup() {
      for (Class<? extends BindableType> interfaceKey : this.dependencies.keySet()) {
         IBindable concreteObject = this.get(interfaceKey, true);
         if (!concreteObject.getDelayedSetupComplete()) {
            concreteObject.finishDelayedSetup();
         }
      }
   }

   static {
      EMPTY_GET_ALL_LIST.add(null);
   }
}
