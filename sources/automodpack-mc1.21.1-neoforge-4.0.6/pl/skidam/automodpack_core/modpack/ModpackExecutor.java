package pl.skidam.automodpack_core.modpack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.utils.CustomThreadFactoryBuilder;

public class ModpackExecutor {
   private final ThreadPoolExecutor CREATION_EXECUTOR = (ThreadPoolExecutor)Executors.newFixedThreadPool(
      Math.max(1, Runtime.getRuntime().availableProcessors() * 2), new CustomThreadFactoryBuilder().setNameFormat("AutoModpackCreation-%d").build()
   );
   public final Map<String, ModpackContent> modpacks = Collections.synchronizedMap(new HashMap<>());

   private ModpackContent init() {
      if (this.isGenerating()) {
         GlobalVariables.LOGGER.error("Called generate() twice!");
         return null;
      } else {
         try {
            if (!Files.exists(GlobalVariables.hostContentModpackDir)) {
               Files.createDirectories(GlobalVariables.hostContentModpackDir);
               Files.createDirectory(GlobalVariables.hostContentModpackDir.resolve("mods"));
               Files.createDirectory(GlobalVariables.hostContentModpackDir.resolve("config"));
               Files.createDirectory(GlobalVariables.hostContentModpackDir.resolve("shaderpacks"));
               Files.createDirectory(GlobalVariables.hostContentModpackDir.resolve("resourcepacks"));
            }
         } catch (IOException var2) {
            var2.printStackTrace();
         }

         Path cwd = Path.of(System.getProperty("user.dir"));
         return new ModpackContent(
            GlobalVariables.serverConfig.modpackName,
            cwd,
            GlobalVariables.hostContentModpackDir,
            GlobalVariables.serverConfig.syncedFiles,
            GlobalVariables.serverConfig.allowEditsInFiles,
            GlobalVariables.serverConfig.forceCopyFilesToStandardLocation,
            this.CREATION_EXECUTOR
         );
      }
   }

   public boolean generateNew(ModpackContent content) {
      if (content == null) {
         return false;
      } else {
         boolean generated = content.create();
         this.modpacks.put(content.getModpackName(), content);
         return generated;
      }
   }

   public boolean generateNew() {
      ModpackContent content = this.init();
      if (content == null) {
         return false;
      } else {
         boolean generated = content.create();
         this.modpacks.put(content.getModpackName(), content);
         return generated;
      }
   }

   public boolean loadLast() {
      ModpackContent content = this.init();
      if (content == null) {
         return false;
      } else {
         boolean generated = content.loadPreviousContent();
         this.modpacks.put(content.getModpackName(), content);
         return generated;
      }
   }

   public boolean isGenerating() {
      int activeCount = this.CREATION_EXECUTOR.getActiveCount();
      int queueSize = this.CREATION_EXECUTOR.getQueue().size();
      return activeCount > 0 || queueSize > 0;
   }

   public ThreadPoolExecutor getExecutor() {
      return this.CREATION_EXECUTOR;
   }

   public void stop() {
      this.CREATION_EXECUTOR.shutdown();
   }
}
