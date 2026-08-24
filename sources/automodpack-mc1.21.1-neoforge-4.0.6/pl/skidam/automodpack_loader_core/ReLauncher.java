package pl.skidam.automodpack_loader_core;

import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.loader.LoaderManagerService;
import pl.skidam.automodpack_loader_core.client.Changelogs;
import pl.skidam.automodpack_loader_core.compat.crashassistant.ProcessSignalIO;
import pl.skidam.automodpack_loader_core.screen.ScreenManager;
import pl.skidam.automodpack_loader_core.utils.UpdateType;

public class ReLauncher {
   private final String updateMessage;
   private final Path modpackDir;
   private final UpdateType updateType;
   private final Changelogs changelogs;

   public ReLauncher(UpdateType updateType) {
      this.modpackDir = null;
      this.updateType = updateType;
      this.changelogs = null;
      this.updateMessage = "Successfully updated AutoModpack!";
   }

   public ReLauncher(Path modpackDir, UpdateType updateType, Changelogs changelogs) {
      this.modpackDir = modpackDir;
      this.updateType = updateType;
      this.changelogs = changelogs;
      this.updateMessage = "Successfully updated the modpack!";
   }

   public final void restart(boolean shutdownInPreload, Runnable... callbacks) {
      if (GlobalVariables.preload && !shutdownInPreload) {
         this.runCallbacks(callbacks);
      } else {
         boolean isClient = GlobalVariables.LOADER_MANAGER.getEnvironmentType() == LoaderManagerService.EnvironmentType.CLIENT;
         boolean isHeadless = GraphicsEnvironment.isHeadless();
         if (isClient) {
            this.handleClientRestart(callbacks, isHeadless);
         } else {
            this.handleServerRestart(callbacks);
         }
      }
   }

   private void handleClientRestart(Runnable[] callbacks, boolean isHeadless) {
      if (this.updateType != null && new ScreenManager().getScreenString().isPresent()) {
         new ScreenManager().restart(this.modpackDir, this.updateType, this.changelogs);
      } else if (GlobalVariables.preload) {
         ProcessSignalIO.post("normal_stop");
         Semaphore semaphore = null;
         Thread shutdownHook = new Thread(() -> this.runCallbacks(callbacks));
         Runtime.getRuntime().addShutdownHook(shutdownHook);
         if (isHeadless) {
            GlobalVariables.LOGGER.info("Please restart the game to apply updates!");
         } else {
            semaphore = new Gui().open(this.updateMessage);
         }

         this.wait(semaphore);
         this.runCallbacks(callbacks);
         Runtime.getRuntime().removeShutdownHook(shutdownHook);
         this.wait(semaphore);
         System.exit(0);
      }

      this.runCallbacks(callbacks);
   }

   private void wait(Semaphore semaphore) {
      if (semaphore != null) {
         try {
            semaphore.acquire();
         } catch (InterruptedException var3) {
            GlobalVariables.LOGGER.error("Failed to acquire semaphore", var3);
         }
      }
   }

   private void handleServerRestart(Runnable[] callbacks) {
      GlobalVariables.LOGGER.info("Please restart the server to apply updates!");
      this.runCallbacks(callbacks);
      System.exit(0);
   }

   private void runCallbacks(Runnable[] callbacks) {
      for (Runnable callback : callbacks) {
         try {
            callback.run();
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }
   }
}
