package pl.skidam.automodpack_loader_core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.loader.LoaderManagerService;
import pl.skidam.automodpack_core.utils.CustomFileUtils;
import pl.skidam.automodpack_core.utils.SemanticVersion;
import pl.skidam.automodpack_loader_core.platforms.ModrinthAPI;
import pl.skidam.automodpack_loader_core.screen.ScreenManager;
import pl.skidam.automodpack_loader_core.utils.DownloadManager;
import pl.skidam.automodpack_loader_core.utils.UpdateType;

public class SelfUpdater {
   public static final String AUTOMODPACK_ID = "k68glP2e";
   private static final SemanticVersion MINIMUM_SAFE_VERSION = new SemanticVersion(4, 0, 0, "release", 2147483647);

   public static boolean update() {
      return update(null);
   }

   public static boolean update(Jsons.ModpackContentFields serverModpackContent) {
      if (GlobalVariables.LOADER_MANAGER.isDevelopmentEnvironment()) {
         return false;
      } else if (GlobalVariables.LOADER_MANAGER.getEnvironmentType() == LoaderManagerService.EnvironmentType.SERVER
         && !GlobalVariables.serverConfig.selfUpdater) {
         GlobalVariables.LOGGER.info("AutoModpack self-updater is disabled in server config.");
         return false;
      } else {
         boolean gettingServerVersion = serverModpackContent != null
            && serverModpackContent.automodpackVersion != null
            && !serverModpackContent.automodpackVersion.isBlank();
         if (!gettingServerVersion
            && GlobalVariables.LOADER_MANAGER.getEnvironmentType() == LoaderManagerService.EnvironmentType.CLIENT
            && !GlobalVariables.clientConfig.selfUpdater) {
            GlobalVariables.LOGGER.info("AutoModpack self-updater is disabled in client config.");
            return false;
         } else {
            SemanticVersion currentVersion;
            try {
               currentVersion = SemanticVersion.parse(GlobalVariables.AM_VERSION);
            } catch (IllegalArgumentException var9) {
               GlobalVariables.LOGGER.error("Current installed AutoModpack version is corrupt/invalid: " + GlobalVariables.AM_VERSION);
               return false;
            }

            List<ModrinthAPI> modrinthAPIList = new ArrayList<>();
            if (gettingServerVersion) {
               if (serverModpackContent.automodpackVersion.equals(GlobalVariables.AM_VERSION)) {
                  GlobalVariables.LOGGER.info("AutoModpack is up-to-date with server version: {}", serverModpackContent.automodpackVersion);
                  return false;
               }

               if (!GlobalVariables.clientConfig.syncAutoModpackVersion) {
                  GlobalVariables.LOGGER.warn("Version syncing disabled. Cannot sync to server version: {}", serverModpackContent.automodpackVersion);
                  return false;
               }

               GlobalVariables.LOGGER.info("Syncing AutoModpack to server version: {}", serverModpackContent.automodpackVersion);
               modrinthAPIList.add(ModrinthAPI.getModSpecificVersion("k68glP2e", serverModpackContent.automodpackVersion, serverModpackContent.mcVersion));
            } else {
               GlobalVariables.LOGGER.info("Checking if AutoModpack is up-to-date...");
               modrinthAPIList = ModrinthAPI.getModInfosFromID("k68glP2e");
            }

            if (modrinthAPIList != null && !modrinthAPIList.isEmpty()) {
               Iterator var4 = modrinthAPIList.iterator();

               while (true) {
                  ModrinthAPI automodpack;
                  String rawRemoteVersion;
                  SemanticVersion remoteVersion;
                  while (true) {
                     if (!var4.hasNext()) {
                        if (!gettingServerVersion) {
                           GlobalVariables.LOGGER.info("No suitable updates found.");
                        }

                        return false;
                     }

                     automodpack = (ModrinthAPI)var4.next();
                     if (automodpack != null && automodpack.fileVersion() != null) {
                        rawRemoteVersion = automodpack.fileVersion();

                        try {
                           remoteVersion = SemanticVersion.parse(rawRemoteVersion);
                           break;
                        } catch (IllegalArgumentException var10) {
                        }
                     }
                  }

                  if (automodpack.SHA1Hash().equals(CustomFileUtils.getHash(GlobalVariables.THIS_MOD_JAR))) {
                     GlobalVariables.LOGGER.info("Already on the target version (Hash match): {}", GlobalVariables.AM_VERSION);
                     return false;
                  }

                  int comparison = remoteVersion.compareTo(currentVersion);
                  if (gettingServerVersion && comparison <= 0) {
                     GlobalVariables.LOGGER
                        .info("Keeping installed AutoModpack {} instead of server version {}.", GlobalVariables.AM_VERSION, rawRemoteVersion);
                     return false;
                  }

                  if (!gettingServerVersion && comparison <= 0) {
                     if (comparison == 0) {
                        GlobalVariables.LOGGER.info("No updates found. You are on the latest version: {}", rawRemoteVersion);
                     } else {
                        GlobalVariables.LOGGER
                           .info("Development check: Installed version {} is newer/different than release {}.", GlobalVariables.AM_VERSION, rawRemoteVersion);
                     }

                     return false;
                  }

                  if (!gettingServerVersion && currentVersion.isStable() && !remoteVersion.isStable()) {
                     GlobalVariables.LOGGER
                        .info("Skipping update: You are on Stable ({}) and latest is Pre-release ({}).", GlobalVariables.AM_VERSION, rawRemoteVersion);
                  } else {
                     if (validUpdate(remoteVersion)) {
                        GlobalVariables.LOGGER.info("Update found! Updating from {} to {}", GlobalVariables.AM_VERSION, rawRemoteVersion);
                        installModVersion(automodpack);
                        return true;
                     }

                     if (gettingServerVersion) {
                        return false;
                     }
                  }
               }
            } else {
               GlobalVariables.LOGGER.warn("Couldn't get version info from Modrinth API.");
               return false;
            }
         }
      }
   }

   public static boolean validUpdate(SemanticVersion remoteVersion) {
      if (remoteVersion.compareTo(MINIMUM_SAFE_VERSION) < 0) {
         GlobalVariables.LOGGER
            .error(
               "Downgrading AutoModpack to version {} is strongly discouraged/disabled due to security concerns (Target is older than 4.0.0 Stable).",
               remoteVersion
            );
         return false;
      } else {
         return true;
      }
   }

   public static void installModVersion(ModrinthAPI automodpack) {
      Path automodpackUpdateJar = GlobalVariables.automodpackDir.resolve(automodpack.fileName());

      try {
         DownloadManager downloadManager = new DownloadManager();
         new ScreenManager().download(downloadManager, "AutoModpack " + automodpack.fileVersion());
         downloadManager.download(
            automodpackUpdateJar,
            automodpack.SHA1Hash(),
            List.of(automodpack.downloadUrl()),
            () -> GlobalVariables.LOGGER.info("Downloaded update for AutoModpack."),
            () -> GlobalVariables.LOGGER.error("Failed to download update for AutoModpack.")
         );
         downloadManager.joinAll();
         downloadManager.cancelAllAndShutdown();
         Path newAutomodpackJar = GlobalVariables.THIS_MOD_JAR.getParent().resolve(automodpackUpdateJar.getFileName());
         UpdateType updateType = UpdateType.AUTOMODPACK;
         ReLauncher relauncher = new ReLauncher(updateType);
         Runnable callback = () -> {
            CustomFileUtils.executeOrder66(GlobalVariables.THIS_MOD_JAR);
            GlobalVariables.LOGGER.info("Successfully updated AutoModpack! Restarting...");
         };
         CustomFileUtils.copyFile(automodpackUpdateJar, newAutomodpackJar);
         CustomFileUtils.executeOrder66(automodpackUpdateJar);
         relauncher.restart(true, callback);
      } catch (Exception var7) {
         GlobalVariables.LOGGER.error("Failed to update! " + var7);
      }
   }
}
