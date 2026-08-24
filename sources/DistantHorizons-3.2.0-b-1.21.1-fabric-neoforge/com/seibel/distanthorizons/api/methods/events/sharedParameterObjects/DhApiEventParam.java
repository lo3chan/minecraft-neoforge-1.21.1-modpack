package com.seibel.distanthorizons.api.methods.events.sharedParameterObjects;

public class DhApiEventParam<T> {
   public final T value;

   public DhApiEventParam(T value) {
      this.value = value;
   }
}
