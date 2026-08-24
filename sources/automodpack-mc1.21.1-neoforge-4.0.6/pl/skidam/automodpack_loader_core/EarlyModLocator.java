package pl.skidam.automodpack_loader_core;

import cpw.mods.jarhandling.JarContents;
import java.nio.file.Path;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import net.neoforged.neoforgespi.locating.IncompatibleFileReporting;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;
import pl.skidam.automodpack_loader_core.mods.ModpackLoader;

public class EarlyModLocator implements IModFileCandidateLocator {
   public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
      ProgressMeter progress = StartupNotificationManager.prependProgressBar("[Automodpack] Preload", 0);
      new Preload();
      progress.complete();

      for (Path path : ModpackLoader.modsToLoad) {
         pipeline.addPath(path, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.WARN_ALWAYS);
         pipeline.readModFile(JarContents.of(path), ModFileDiscoveryAttributes.DEFAULT);
      }
   }

   public int getPriority() {
      return 1000;
   }
}
