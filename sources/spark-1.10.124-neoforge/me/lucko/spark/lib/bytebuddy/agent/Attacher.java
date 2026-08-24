package me.lucko.spark.lib.bytebuddy.agent;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import me.lucko.spark.lib.bytebuddy.agent.utility.nullability.MaybeNull;

public class Attacher {
   public static final String DUMP_PROPERTY = "me.lucko.spark.lib.bytebuddy.agent.attacher.dump";

   private Attacher() {
      throw new UnsupportedOperationException("This class is a utility class and not supposed to be instantiated");
   }

   @SuppressFBWarnings(
      value = {"REC_CATCH_EXCEPTION"},
      justification = "Exception should not be rethrown but trigger a fallback."
   )
   public static void main(String[] args) {
      try {
         String argument;
         if (args.length >= 5 && args[4].length() != 0) {
            StringBuilder stringBuilder = new StringBuilder(args[4].substring(1));

            for (int index = 5; index < args.length; index++) {
               stringBuilder.append(' ').append(args[index]);
            }

            argument = stringBuilder.toString();
         } else {
            argument = null;
         }

         install(Class.forName(args[0]), args[1], args[2], Boolean.parseBoolean(args[3]), argument);
      } catch (Throwable var10) {
         Throwable throwable = var10;

         try {
            String property = System.getProperty("me.lucko.spark.lib.bytebuddy.agent.attacher.dump");
            if (property != null && property.length() > 0) {
               PrintStream outputStream = new PrintStream(new FileOutputStream(property, true), false, "UTF-8");

               try {
                  throwable.printStackTrace(outputStream);
               } finally {
                  outputStream.close();
               }
            }
         } catch (Throwable var9) {
         }

         System.exit(1);
      }
   }

   protected static void install(Class<?> virtualMachineType, String processId, String agent, boolean isNative, @MaybeNull String argument) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      Object virtualMachineInstance = virtualMachineType.getMethod("attach", String.class).invoke(null, processId);

      try {
         virtualMachineType.getMethod(isNative ? "loadAgentPath" : "loadAgent", String.class, String.class).invoke(virtualMachineInstance, agent, argument);
      } finally {
         virtualMachineType.getMethod("detach").invoke(virtualMachineInstance);
      }
   }
}
