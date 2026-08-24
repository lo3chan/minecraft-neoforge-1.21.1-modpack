package dev.tr7zw.notenoughanimations.versionless;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tr7zw.notenoughanimations.versionless.animations.BowAnimation;
import dev.tr7zw.notenoughanimations.versionless.config.Config;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NEABaseMod {
   public static final Logger LOGGER = LogManager.getLogger("NotEnoughAnimations");
   public static Config config;
   protected final File settingsFile = new File("config", "notenoughanimations.json");
   protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

   protected void setupConfig() {
      try {
         Class clientClass = Class.forName("dev.tr7zw.firstperson.FirstPersonModelCore");
         config.bowAnimation = BowAnimation.CUSTOM_V1;
      } catch (Throwable var2) {
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
}
