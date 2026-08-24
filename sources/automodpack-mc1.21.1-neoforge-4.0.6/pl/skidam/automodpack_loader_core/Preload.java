package pl.skidam.automodpack_loader_core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.auth.Secrets;
import pl.skidam.automodpack_core.auth.SecretsStore;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.ConfigUtils;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.loader.LoaderManagerService;
import pl.skidam.automodpack_core.utils.AddressHelpers;
import pl.skidam.automodpack_core.utils.ClientCacheUtils;
import pl.skidam.automodpack_core.utils.FileInspection;
import pl.skidam.automodpack_core.utils.JarUtils;
import pl.skidam.automodpack_core.utils.ModpackContentTools;
import pl.skidam.automodpack_loader_core.client.ModpackUpdater;
import pl.skidam.automodpack_loader_core.client.ModpackUtils;
import pl.skidam.automodpack_loader_core.loader.LoaderManager;
import pl.skidam.automodpack_loader_core.mods.ModpackLoader;

public class Preload {
   public Preload() {
      try {
         long start = System.currentTimeMillis();
         GlobalVariables.LOGGER.info("Prelaunching AutoModpack...");
         this.initializeGlobalVariables();
         this.loadConfigs();
         this.updateAll();
         GlobalVariables.LOGGER.info("AutoModpack prelaunched! took " + (System.currentTimeMillis() - start) + "ms");
      } catch (Exception var3) {
         var3.printStackTrace();
         throw new RuntimeException(var3);
      }
   }

   private void updateAll() {
      Optional<Path> optionalSelectedModpackDir = ModpackContentTools.getModpackDir(GlobalVariables.clientConfig.selectedModpack);
      if (GlobalVariables.LOADER_MANAGER.getEnvironmentType() != LoaderManagerService.EnvironmentType.SERVER && !optionalSelectedModpackDir.isEmpty()) {
         GlobalVariables.selectedModpackDir = optionalSelectedModpackDir.get();
         InetSocketAddress selectedModpackAddress = null;
         InetSocketAddress selectedServerAddress = null;
         boolean requiresMagic = true;
         if (!GlobalVariables.clientConfig.selectedModpack.isBlank()
            && GlobalVariables.clientConfig.installedModpacks.containsKey(GlobalVariables.clientConfig.selectedModpack)) {
            Jsons.ModpackAddresses entry = GlobalVariables.clientConfig.installedModpacks.get(GlobalVariables.clientConfig.selectedModpack);
            selectedModpackAddress = entry.hostAddress;
            selectedServerAddress = entry.serverAddress;
            requiresMagic = entry.requiresMagic;
         }

         if (selectedModpackAddress == null) {
            SelfUpdater.update();
            ClientCacheUtils.deleteDummyFiles();
         } else {
            Secrets.Secret secret = SecretsStore.getClientSecret(GlobalVariables.clientConfig.selectedModpack);
            Jsons.ModpackAddresses modpackAddresses = new Jsons.ModpackAddresses(selectedModpackAddress, selectedServerAddress, requiresMagic);
            Optional<Jsons.ModpackContentFields> optionalLatestModpackContent = ModpackUtils.requestServerModpackContent(modpackAddresses, secret, false);
            Jsons.ModpackContentFields latestModpackContent = ConfigTools.loadModpackContent(
               GlobalVariables.selectedModpackDir.resolve(GlobalVariables.hostModpackContentFile.getFileName())
            );
            if (optionalLatestModpackContent.isPresent()) {
               latestModpackContent = optionalLatestModpackContent.get();
               if (SelfUpdater.update(latestModpackContent)) {
                  return;
               }
            }

            ClientCacheUtils.deleteDummyFiles();
            if (GlobalVariables.clientConfig.updateSelectedModpackOnLaunch) {
               new ModpackUpdater(latestModpackContent, modpackAddresses, secret, GlobalVariables.selectedModpackDir).processModpackUpdate(null);
            }
         }
      } else {
         SelfUpdater.update();
      }
   }

   private void initializeGlobalVariables() {
      GlobalVariables.preload = true;
      GlobalVariables.LOADER_MANAGER = new LoaderManager();
      GlobalVariables.MODPACK_LOADER = new ModpackLoader();
      GlobalVariables.MC_VERSION = GlobalVariables.LOADER_MANAGER.getModVersion("minecraft");
      GlobalVariables.LOADER_VERSION = GlobalVariables.LOADER_MANAGER.getLoaderVersion();
      GlobalVariables.LOADER = GlobalVariables.LOADER_MANAGER.getPlatformType().toString().toLowerCase();
      GlobalVariables.THIS_MOD_JAR = JarUtils.getJarPath(this.getClass());
      GlobalVariables.AM_VERSION = FileInspection.getModVersion(GlobalVariables.THIS_MOD_JAR);
      GlobalVariables.MODS_DIR = GlobalVariables.THIS_MOD_JAR.getParent();
   }

   private void loadConfigs() {
      long startTime = System.currentTimeMillis();
      Jsons.VersionConfigField clientConfigVersion = ConfigTools.softLoad(GlobalVariables.clientConfigFile, Jsons.VersionConfigField.class);
      if (clientConfigVersion != null && clientConfigVersion.DO_NOT_CHANGE_IT == 1) {
         Jsons.ClientConfigFieldsV1 clientConfigV1 = ConfigTools.load(GlobalVariables.clientConfigFile, Jsons.ClientConfigFieldsV1.class);
         if (clientConfigV1 != null) {
            clientConfigVersion.DO_NOT_CHANGE_IT = 2;
            clientConfigV1.DO_NOT_CHANGE_IT = 2;
            clientConfigV1.installedModpacks = null;
         }

         ConfigTools.save(GlobalVariables.clientConfigFile, clientConfigV1);
         GlobalVariables.LOGGER.info("Updated client config version to {}", clientConfigVersion.DO_NOT_CHANGE_IT);
      }

      GlobalVariables.clientConfig = ConfigTools.load(GlobalVariables.clientConfigFile, Jsons.ClientConfigFieldsV2.class);
      Jsons.VersionConfigField serverConfigVersion = ConfigTools.softLoad(GlobalVariables.serverConfigFile, Jsons.VersionConfigField.class);
      if (serverConfigVersion != null && serverConfigVersion.DO_NOT_CHANGE_IT == 1) {
         Jsons.ServerConfigFieldsV1 serverConfigV1 = ConfigTools.load(GlobalVariables.serverConfigFile, Jsons.ServerConfigFieldsV1.class);
         Jsons.ServerConfigFieldsV2 serverConfigV2 = ConfigTools.softLoad(GlobalVariables.serverConfigFile, Jsons.ServerConfigFieldsV2.class);
         if (serverConfigV1 != null && serverConfigV2 != null) {
            serverConfigVersion.DO_NOT_CHANGE_IT = 2;
            serverConfigV2.DO_NOT_CHANGE_IT = 2;
            if (serverConfigV1.hostIp.isBlank()) {
               serverConfigV2.addressToSend = "";
            } else {
               serverConfigV2.addressToSend = AddressHelpers.parse(serverConfigV1.hostIp).getHostString();
            }

            if (serverConfigV1.hostModpackOnMinecraftPort) {
               serverConfigV2.bindPort = -1;
               serverConfigV2.portToSend = -1;
            } else {
               serverConfigV2.bindPort = serverConfigV1.hostPort;
               serverConfigV2.portToSend = serverConfigV1.hostPort;
            }
         }

         ConfigTools.save(GlobalVariables.serverConfigFile, serverConfigV2);
         GlobalVariables.LOGGER.info("Updated server config version to {}", serverConfigVersion.DO_NOT_CHANGE_IT);
      }

      GlobalVariables.serverConfig = ConfigTools.load(GlobalVariables.serverConfigFile, Jsons.ServerConfigFieldsV2.class);
      if (GlobalVariables.serverConfig != null) {
         if (GlobalVariables.serverConfig.acceptedLoaders == null) {
            GlobalVariables.serverConfig.acceptedLoaders = Set.of(GlobalVariables.LOADER);
         } else if (!GlobalVariables.serverConfig.acceptedLoaders.contains(GlobalVariables.LOADER)) {
            GlobalVariables.serverConfig.acceptedLoaders.add(GlobalVariables.LOADER);
         }

         if (!GlobalVariables.serverConfig.modpackName.isEmpty() && FileInspection.isInValidFileName(GlobalVariables.serverConfig.modpackName)) {
            GlobalVariables.serverConfig.modpackName = FileInspection.fixFileName(GlobalVariables.serverConfig.modpackName);
            GlobalVariables.LOGGER.info("Changed modpack name to {}", GlobalVariables.serverConfig.modpackName);
         }

         ConfigUtils.normalizeServerConfig(GlobalVariables.serverConfig);
         ConfigTools.save(GlobalVariables.serverConfigFile, GlobalVariables.serverConfig);
      }

      if (GlobalVariables.clientConfig != null) {
         if (GlobalVariables.clientConfig.installedModpacks == null) {
            GlobalVariables.clientConfig.installedModpacks = new HashMap<>();
         }

         if (GlobalVariables.clientConfig.selectedModpack == null) {
            GlobalVariables.clientConfig.selectedModpack = "";
         }

         ConfigTools.save(GlobalVariables.clientConfigFile, GlobalVariables.clientConfig);
      }

      GlobalVariables.knownHosts = ConfigTools.load(GlobalVariables.knownHostsFile, Jsons.KnownHostsFields.class);
      if (GlobalVariables.knownHosts != null && GlobalVariables.knownHosts.hosts == null) {
         GlobalVariables.knownHosts.hosts = new HashMap<>();
      }

      try {
         Files.createDirectories(GlobalVariables.privateDir);
         String os = System.getProperty("os.name").toLowerCase();

         try {
            if (os.contains("win")) {
               Files.setAttribute(GlobalVariables.privateDir, "dos:hidden", true);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("mac")) {
               Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwx------");
               Files.setPosixFilePermissions(GlobalVariables.privateDir, perms);
            }
         } catch (IOException | UnsupportedOperationException var7) {
            GlobalVariables.LOGGER.debug("Failed to set private directory attributes for os: {}", os);
         }
      } catch (IOException var8) {
         GlobalVariables.LOGGER.error("Failed to create private directory", var8);
      }

      if (GlobalVariables.serverConfig != null && GlobalVariables.clientConfig != null) {
         GlobalVariables.LOGGER.info("Loaded config! took {}ms", System.currentTimeMillis() - startTime);
      } else {
         throw new RuntimeException("Failed to load config!");
      }
   }
}
