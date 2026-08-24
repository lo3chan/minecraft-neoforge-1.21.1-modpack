package net.conczin.immersive_gateways;

import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

public class IrisCompat {
   private static BooleanSupplier shaderCheck;

   public static boolean isShaderPackInUse() {
      return shaderCheck.getAsBoolean();
   }

   static {
      try {
         Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
         Method getInstance = irisApiClass.getMethod("getInstance");
         Method isShaderPackInUse = irisApiClass.getMethod("isShaderPackInUse");
         shaderCheck = () -> {
            try {
               Object instance = getInstance.invoke(null);
               return (Boolean)isShaderPackInUse.invoke(instance);
            } catch (Exception var3x) {
               return false;
            }
         };
      } catch (NoSuchMethodException | ClassNotFoundException var3) {
         shaderCheck = () -> false;
      }
   }
}
