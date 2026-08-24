package org.dimdev.limlib.impl.bridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.dimdev.limlib.impl.Limlib;

public class IrisBridge {
   public static final boolean IRIS_LOADED = Limlib.isModLoaded("iris");

   public static boolean areShadersInUse() {
      if (Limlib.isModLoaded("iris")) {
         try {
            Class<?> irisApi = Class.forName("net.irisshaders.iris.apiimpl.IrisApiV0Impl");
            Field irisInstance = irisApi.getField("INSTANCE");
            Method isShaderInUse = irisApi.getMethod("isShaderPackInUse");
            if (isShaderInUse.invoke(irisInstance.get(null)) instanceof Boolean depends) {
               return depends;
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return false;
         }
      }

      return false;
   }
}
