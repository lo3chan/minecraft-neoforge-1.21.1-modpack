package com.iafenvoy.jupiter;

import com.iafenvoy.jupiter.compat.ExtraConfigManager;
import com.iafenvoy.jupiter.compat.clothconfig.ClothConfigLoader;
import com.iafenvoy.jupiter.compat.forgeconfigspec.ConfigSpecLoader;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.internal.JupiterSettings;
import com.iafenvoy.jupiter.network.ClientConfigNetwork;
import com.iafenvoy.jupiter.network.ServerConfigNetwork;
import com.iafenvoy.jupiter.network.ServerNetworkHelper;
import com.iafenvoy.jupiter.network.payload.ConfigErrorPayload;
import com.iafenvoy.jupiter.network.payload.ConfigRequestPayload;
import com.iafenvoy.jupiter.network.payload.ConfigSyncPayload;
import com.iafenvoy.jupiter.test.TestConfig;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class Jupiter {
   public static final String MOD_ID = "jupiter";
   public static final Logger LOGGER = LogUtils.getLogger();

   public static void init(boolean development) {
      ServerNetworkHelper.INSTANCE.registerPayloadType(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);
      ServerNetworkHelper.INSTANCE.registerPayloadType(ConfigRequestPayload.TYPE, ConfigRequestPayload.CODEC);
      ServerNetworkHelper.INSTANCE.registerPayloadType(ConfigErrorPayload.TYPE, ConfigErrorPayload.CODEC);
      ConfigManager.getInstance().registerServerConfigHandler(JupiterSettings.INSTANCE, ServerConfigManager.PermissionChecker.IS_OPERATOR);
      if (development) {
         ConfigManager.getInstance().registerConfigHandler(new TestConfig());
      }

      if (Platform.isModLoaded("forge") || Platform.isModLoaded("neoforge") || Platform.isModLoaded("forgeconfigapiport")) {
         LOGGER.info("Config spec system detected, register to Jupiter Config System.");

         try {
            ExtraConfigManager.registerScanner(ConfigSource.NIGHT_CONFIG, ConfigSpecLoader::scanConfig);
         } catch (Exception var3) {
            LOGGER.error("Failed to register config spec loader", var3);
         }
      }

      if (Platform.isModLoaded("cloth-config")
         || Platform.isModLoaded("cloth-config2")
         || Platform.isModLoaded("cloth_config")
         || Platform.isModLoaded("cloth_config2")) {
         LOGGER.info("Cloth Config API detected, register to Jupiter Config System.");

         try {
            ExtraConfigManager.registerScanner(ConfigSource.CLOTH_CONFIG, ClothConfigLoader::scanConfig);
         } catch (Exception var2) {
            LOGGER.error("Failed to register Cloth Config loader", var2);
         }
      }
   }

   public static void process() {
      ServerConfigNetwork.init();
   }

   public static void processClient() {
      ClientConfigNetwork.init();
   }
}
