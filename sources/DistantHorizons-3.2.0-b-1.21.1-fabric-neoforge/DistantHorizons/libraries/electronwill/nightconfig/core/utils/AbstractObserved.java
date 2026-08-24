package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

abstract class AbstractObserved {
   protected final Runnable callback;

   protected AbstractObserved(Runnable callback) {
      this.callback = callback;
   }
}
