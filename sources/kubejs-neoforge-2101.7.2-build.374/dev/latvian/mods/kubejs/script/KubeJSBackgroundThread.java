package dev.latvian.mods.kubejs.script;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KubeJSBackgroundThread extends Thread {
   public static boolean running = true;

   public KubeJSBackgroundThread() {
      super("kubejs-background-thread");
      this.setDaemon(true);
   }

   @Override
   public void run() {
      Runtime.getRuntime().addShutdownHook(new Thread(KubeJSBackgroundThread::shutdown, "kubejs-background-thread-shutdown"));

      for (ScriptType type : ScriptType.VALUES) {
         type.executor = Executors.newSingleThreadExecutor(Thread.ofVirtual().name("kubejs-" + type + "-background-task-").factory());
      }

      while (running) {
         try {
            Thread.sleep(3000L);
         } catch (InterruptedException var9) {
            var9.printStackTrace();
         }

         for (ScriptType type : ScriptType.VALUES) {
            type.console.flush(false);
         }
      }

      for (ScriptType type : ScriptType.VALUES) {
         type.console.flush(true);
         if (type.executor instanceof ExecutorService service) {
            service.shutdown();

            boolean b;
            try {
               b = service.awaitTermination(3L, TimeUnit.SECONDS);
            } catch (InterruptedException var8) {
               b = false;
            }

            if (!b) {
               service.shutdownNow();
            }
         }
      }
   }

   public static void shutdown() {
      running = false;

      for (ScriptType value : ScriptType.VALUES) {
         value.console.flush(true);
      }
   }
}
