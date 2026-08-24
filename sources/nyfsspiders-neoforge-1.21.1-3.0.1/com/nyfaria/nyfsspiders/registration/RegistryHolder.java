package com.nyfaria.nyfsspiders.registration;

import java.util.ServiceLoader;

public interface RegistryHolder {
   static void loadAll() {
      ServiceLoader.load(RegistryHolder.class).forEach(clz -> clz.getClass().getName());
   }
}
