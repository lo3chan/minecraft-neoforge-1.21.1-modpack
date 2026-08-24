package pl.skidam.automodpack_core;

import java.nio.file.Path;
import java.util.HashSet;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.modpack.ModpackContent;
import pl.skidam.automodpack_core.modpack.ModpackExecutor;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;

public class Server {
   public static void main(String[] args) {
      if (args.length < 1) {
         GlobalVariables.LOGGER.error("Modpack id not provided!");
      } else {
         NettyServer server = new NettyServer();
         GlobalVariables.hostServer = server;
         String modpackDirStr = args[0];
         Path cwd = Path.of(System.getProperty("user.dir"));
         Path modpackDir = cwd.resolve("modpacks").resolve(modpackDirStr);
         modpackDir.toFile().mkdirs();
         GlobalVariables.hostModpackContentFile = modpackDir.resolve("automodpack-content.json");
         GlobalVariables.serverConfigFile = modpackDir.resolve("automodpack-server.json");
         GlobalVariables.serverCoreConfigFile = modpackDir.resolve("automodpack-core.json");
         GlobalVariables.serverConfig = ConfigTools.load(GlobalVariables.serverConfigFile, Jsons.ServerConfigFieldsV2.class);
         if (GlobalVariables.serverConfig != null) {
            GlobalVariables.serverConfig.syncedFiles = new HashSet<>();
            GlobalVariables.serverConfig.validateSecrets = false;
            ConfigTools.save(GlobalVariables.serverConfigFile, GlobalVariables.serverConfig);
            if (GlobalVariables.serverConfig.bindPort == -1) {
               GlobalVariables.LOGGER.error("Host port not set in config!");
               return;
            }
         }

         Jsons.ServerCoreConfigFields serverCoreConfig = ConfigTools.load(GlobalVariables.serverCoreConfigFile, Jsons.ServerCoreConfigFields.class);
         if (serverCoreConfig != null) {
            GlobalVariables.AM_VERSION = serverCoreConfig.automodpackVersion;
            GlobalVariables.LOADER = serverCoreConfig.loader;
            GlobalVariables.LOADER_VERSION = serverCoreConfig.loaderVersion;
            GlobalVariables.MC_VERSION = serverCoreConfig.mcVersion;
            ConfigTools.save(GlobalVariables.serverCoreConfigFile, serverCoreConfig);
         }

         Path mainModpackDir = modpackDir.resolve("main");
         mainModpackDir.toFile().mkdirs();
         ModpackExecutor modpackExecutor = new ModpackExecutor();
         ModpackContent modpackContent = new ModpackContent(
            GlobalVariables.serverConfig.modpackName,
            null,
            mainModpackDir,
            GlobalVariables.serverConfig.syncedFiles,
            GlobalVariables.serverConfig.allowEditsInFiles,
            GlobalVariables.serverConfig.forceCopyFilesToStandardLocation,
            modpackExecutor.getExecutor()
         );
         boolean generated = modpackExecutor.generateNew(modpackContent);
         if (generated) {
            GlobalVariables.LOGGER.info("Modpack generated!");
         } else {
            GlobalVariables.LOGGER.error("Failed to generate modpack!");
         }

         modpackExecutor.stop();
         GlobalVariables.LOGGER.info("Starting server on port {}", GlobalVariables.serverConfig.bindPort);
         server.start();

         while (server.isRunning()) {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var11) {
               GlobalVariables.LOGGER.error("Interrupted server thread", var11);
            }
         }
      }
   }
}
