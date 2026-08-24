package dev.latvian.mods.kubejs.script;

import java.io.IOException;
import java.nio.file.Files;

public class KubeJSFileWatcherThread extends Thread {
   public final ScriptType scriptType;
   public final ScriptFile[] files;
   public final Runnable reload;

   public KubeJSFileWatcherThread(ScriptType scriptType, ScriptFile[] files, Runnable reload) {
      super("KubeJS File Watcher");
      this.setDaemon(true);
      this.scriptType = scriptType;
      this.files = files;
      this.reload = reload;
   }

   @Override
   public void run() {
      this.scriptType.console.info("#%08X Started watching %d files".formatted(this.hashCode(), this.files.length));

      try {
         Thread.sleep(3000L);
      } catch (InterruptedException var10) {
         var10.printStackTrace();
      }

      while (this.scriptType.fileWatcherThread == this) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var9) {
            var9.printStackTrace();
         }

         boolean changed = false;

         for (ScriptFile file : this.files) {
            try {
               long ms = Files.getLastModifiedTime(file.info.path).toMillis();
               if (file.lastModified != ms) {
                  file.lastModified = ms;
                  changed = true;
               }
            } catch (IOException var8) {
            }
         }

         if (changed) {
            this.scriptType.console.info("#%08X File change detected, reloading scripts...".formatted(this.hashCode()));
            this.reload.run();
            return;
         }
      }
   }
}
