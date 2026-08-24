package com.seibel.distanthorizons.common.commonMixins;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import com.seibel.distanthorizons.api.enums.config.EDhApiUpdateBranch;
import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.updater.UpdateModScreen_fabric;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.installer.GitlabGetter;
import com.seibel.distanthorizons.core.jar.installer.ModrinthGetter;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import java.util.ArrayList;
import net.minecraft.class_310;
import net.minecraft.class_442;

public class DhUpdateScreenBase_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final class_310 MC = class_310.method_1551();

   public static void tryShowUpdateScreenAndRunAutoUpdateStartup(Runnable runnable) {
      boolean newUpdateAvailable = SelfUpdater.onStart();
      if (newUpdateAvailable) {
         runnable = () -> {
            EDhApiUpdateBranch updateBranch = EDhApiUpdateBranch.convertAutoToStableOrNightly(
               com.seibel.distanthorizons.core.config.Config.Client.Advanced.AutoUpdater.updateBranch.get()
            );
            String versionId;
            if (updateBranch == EDhApiUpdateBranch.STABLE) {
               versionId = ModrinthGetter.getLatestIDForVersion(SingletonInjector.INSTANCE.get(IVersionConstants.class).getMinecraftVersion());
            } else {
               ArrayList<Config> pipelines = GitlabGetter.INSTANCE.projectPipelines;
               if (pipelines != null && pipelines.size() > 0) {
                  versionId = pipelines.get(0).get("sha");
               } else {
                  versionId = null;
               }
            }

            if (versionId == null) {
               LOGGER.info("Unable to find new DH update for the [" + updateBranch + "] branch. Assuming DH is up to date...");
            } else {
               RenderThreadTaskHandler.INSTANCE.queueRunningOnRenderThread("Update Screen", () -> {
                  try {
                     DhScreenUtil_fabric.setScreen(new UpdateModScreen_fabric(new class_442(false), versionId));
                  } catch (Exception var2x) {
                     LOGGER.error("Unable to show DH update screen, reason: [" + var2x.getMessage() + "].");
                  }
               });
            }
         };
         runnable.run();
      }
   }
}
