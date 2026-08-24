package dev.tr7zw.waveycapes.versionless;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tr7zw.waveycapes.versionless.config.Config;
import dev.tr7zw.waveycapes.versionless.config.ConfigUpgrader;
import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import lombok.Generated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ModBase {
   public static final Logger LOGGER = LogManager.getLogger("WaveyCapes");
   public static Config config;
   private final File settingsFile = new File("config", "waveycapes.json");
   private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   public static ModBase INSTANCE;

   public void init() {
      INSTANCE = this;
      if (this.settingsFile.exists()) {
         try {
            config = (Config)this.gson.fromJson(new String(Files.readAllBytes(this.settingsFile.toPath()), StandardCharsets.UTF_8), Config.class);
         } catch (Exception var2) {
            System.out.println("Error while loading config! Creating a new one!");
            var2.printStackTrace();
         }
      }

      if (config == null) {
         config = new Config();
         this.writeConfig();
      } else if (ConfigUpgrader.upgradeConfig(config)) {
         this.writeConfig();
      }
   }

   public void writeConfig() {
      if (this.settingsFile.exists()) {
         this.settingsFile.delete();
      }

      try {
         Files.write(this.settingsFile.toPath(), this.gson.toJson(config).getBytes(StandardCharsets.UTF_8));
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   public abstract void initSupportHooks();

   protected static boolean doesClassExist(String name) {
      try {
         if (Class.forName(name) != null) {
            return true;
         }
      } catch (ClassNotFoundException var2) {
      }

      return false;
   }

   public abstract Vector3 applyModAnimations(MinecraftPlayer var1, Vector3 var2);

   @Generated
   public static ModBase getINSTANCE() {
      return INSTANCE;
   }
}
