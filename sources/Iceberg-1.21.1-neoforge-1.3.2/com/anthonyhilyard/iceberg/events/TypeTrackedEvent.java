package com.anthonyhilyard.iceberg.events;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;

public class TypeTrackedEvent<S, T> extends Event<T> {
   private Map<Class<? extends S>, T> listenerTypes = Maps.newHashMap();

   public TypeTrackedEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
      super(type, invokerFactory);
   }

   @Override
   public void register(T listener) {
      throw new UnsupportedOperationException("Register(listener) unsupported.  Use Register(type, listener) instead!");
   }

   public void register(Class<? extends S> type, T listener) {
      super.register(listener);
      this.listenerTypes.put(type, listener);
   }

   public Map<Class<? extends S>, T> getListenerTypes() {
      return this.listenerTypes;
   }
}
