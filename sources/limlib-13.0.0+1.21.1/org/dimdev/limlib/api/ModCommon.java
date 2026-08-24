package org.dimdev.limlib.api;

public interface ModCommon<T extends ISided<?>> {
   void init(T var1);

   String getModId();
}
