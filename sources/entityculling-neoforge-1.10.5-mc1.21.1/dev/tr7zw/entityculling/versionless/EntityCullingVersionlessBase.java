package dev.tr7zw.entityculling.versionless;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EntityCullingVersionlessBase {
   public static final Logger LOGGER = LogManager.getLogger("EntityCulling");
   public OcclusionCullingInstance culling;
   public boolean debugHitboxes = false;
   public static boolean enabled = true;
   protected Thread cullThread;
   protected boolean pressed = false;
   protected boolean pressedBox = false;
   protected boolean lateInit = false;
   public Config config;
   protected final File settingsFile = new File("config", "entityculling.json");
   protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   public int renderedBlockEntities = 0;
   public int skippedBlockEntities = 0;
   public int renderedEntities = 0;
   public int skippedEntities = 0;
   public int tickedEntities = 0;
   public int skippedEntityTicks = 0;

   public void onInitialize() {
      if (this.settingsFile.exists()) {
         try {
            this.config = (Config)this.gson.fromJson(new String(Files.readAllBytes(this.settingsFile.toPath()), StandardCharsets.UTF_8), Config.class);
         } catch (Exception var2) {
            LOGGER.error("Error while loading config! Creating a new one!", var2);
         }
      }

      if (this.config == null) {
         this.config = new Config();
         this.writeConfig();
      } else if (ConfigUpgrader.upgradeConfig(this.config)) {
         this.writeConfig();
      }
   }

   public void writeConfig() {
      if (this.settingsFile.exists()) {
         this.settingsFile.delete();
      }

      try {
         Files.write(this.settingsFile.toPath(), this.gson.toJson(this.config).getBytes(StandardCharsets.UTF_8));
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   public abstract void initModloader();
}
