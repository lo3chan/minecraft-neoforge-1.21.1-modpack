package com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection;

public interface IBindable {
   default void finishDelayedSetup() {
   }

   default boolean getDelayedSetupComplete() {
      return true;
   }
}
