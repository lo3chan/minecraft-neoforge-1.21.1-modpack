package me.lucko.spark.common.util.classfinder;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.util.JavaVersion;
import me.lucko.spark.lib.bytebuddy.agent.ByteBuddyAgent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class InstrumentationClassFinder implements ClassFinder {
   private static boolean warned = false;
   private final Map<String, Class<?>> classes = new HashMap<>();

   private static Instrumentation loadInstrumentation(SparkPlugin plugin) {
      Instrumentation instrumentation = null;

      try {
         instrumentation = ByteBuddyAgent.install();
         if (!warned && JavaVersion.getJavaVersion() >= 21) {
            warned = true;
            plugin.log(Level.INFO, "If you see a warning above that says \"WARNING: A Java agent has been loaded dynamically\", it can be safely ignored.");
            plugin.log(Level.INFO, "See here for more information: https://spark.lucko.me/docs/misc/Java-agent-warning");
         }
      } catch (Exception var3) {
      }

      return instrumentation;
   }

   public InstrumentationClassFinder(SparkPlugin plugin) {
      this(loadInstrumentation(plugin));
   }

   public InstrumentationClassFinder(Instrumentation instrumentation) {
      if (instrumentation != null) {
         for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {
            this.classes.put(loadedClass.getName(), loadedClass);
         }
      }
   }

   @Nullable
   @Override
   public Class<?> findClass(String className) {
      return this.classes.get(className);
   }
}
