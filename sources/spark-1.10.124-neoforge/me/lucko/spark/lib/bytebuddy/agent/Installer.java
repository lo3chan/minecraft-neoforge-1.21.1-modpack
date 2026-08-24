package me.lucko.spark.lib.bytebuddy.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import me.lucko.spark.lib.bytebuddy.agent.utility.nullability.MaybeNull;

public class Installer {
   @MaybeNull
   private static volatile Instrumentation instrumentation;

   private Installer() {
      throw new UnsupportedOperationException("This class is a utility class and not supposed to be instantiated");
   }

   public static Instrumentation getInstrumentation() {
      try {
         Object securityManager = System.class.getMethod("getSecurityManager").invoke(null);
         if (securityManager != null) {
            Class.forName("java.lang.SecurityManager")
               .getMethod("checkPermission", Permission.class)
               .invoke(securityManager, new RuntimePermission("me.lucko.spark.lib.bytebuddy.agent.getInstrumentation"));
         }
      } catch (NoSuchMethodException var2) {
      } catch (ClassNotFoundException var3) {
      } catch (InvocationTargetException var4) {
         Throwable cause = var4.getTargetException();
         if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
         }

         throw new IllegalStateException("Failed to assert access rights using security manager", cause);
      } catch (IllegalAccessException var5) {
         throw new IllegalStateException("Failed to access security manager", var5);
      }

      Instrumentation instrumentation = Installer.instrumentation;
      if (instrumentation == null) {
         throw new IllegalStateException("The Byte Buddy agent is not loaded or this method is not called via the system class loader");
      } else {
         return instrumentation;
      }
   }

   public static void premain(String arguments, Instrumentation instrumentation) {
      Installer.instrumentation = instrumentation;
   }

   public static void agentmain(String arguments, Instrumentation instrumentation) {
      Installer.instrumentation = instrumentation;
   }
}
