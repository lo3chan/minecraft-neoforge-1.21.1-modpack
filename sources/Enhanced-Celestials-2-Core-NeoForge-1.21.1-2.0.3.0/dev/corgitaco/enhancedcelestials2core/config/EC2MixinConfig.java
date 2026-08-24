package dev.corgitaco.enhancedcelestials2core.config;

import com.google.common.base.Suppliers;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Supplier;

public class EC2MixinConfig {
   private static final String DEFAULT_PROPERTIES = "# Enhanced Celestials mixin toggles.\n\n# Overrides Minecraft's default time command with a protected/educated version of the time commands.\n# /time set <day/midnight/noon/night/<time_value>> will now set the time to the next occurrence of that time for the next Minecraft day.\n# /time set <day/midnight/noon/night/<time_value>> now has a 3rd argument that is \"reset\" which is a <true/false> value that when set to true will make this command function like its vanilla counterpart, resetting the the day time counter back to the first MC day.\n# Output messages have been updated to be more clear about what the command is doing.\n# This is enabled by default, but can be disabled if this mixin conflicts with another mod.\nTimeCommandMixin=true\n";
   private static final Supplier<Path> EXTERNAL_PROPERTIES_PATH = Suppliers.memoize(() -> Paths.get("./config/enhancedcelestials2core-mixins.properties"));
   private final Properties properties = new Properties(loadDefaults());

   public void onLoad() {
      try {
         if (Files.notExists(EXTERNAL_PROPERTIES_PATH.get())) {
            Files.createDirectories(EXTERNAL_PROPERTIES_PATH.get().getParent());
            Files.writeString(
               EXTERNAL_PROPERTIES_PATH.get(),
               "# Enhanced Celestials mixin toggles.\n\n# Overrides Minecraft's default time command with a protected/educated version of the time commands.\n# /time set <day/midnight/noon/night/<time_value>> will now set the time to the next occurrence of that time for the next Minecraft day.\n# /time set <day/midnight/noon/night/<time_value>> now has a 3rd argument that is \"reset\" which is a <true/false> value that when set to true will make this command function like its vanilla counterpart, resetting the the day time counter back to the first MC day.\n# Output messages have been updated to be more clear about what the command is doing.\n# This is enabled by default, but can be disabled if this mixin conflicts with another mod.\nTimeCommandMixin=true\n",
               StandardCharsets.UTF_8
            );
         }

         try (StringReader in = new StringReader(Files.readString(EXTERNAL_PROPERTIES_PATH.get(), StandardCharsets.UTF_8))) {
            this.properties.load(in);
         }
      } catch (IOException var6) {
         throw new RuntimeException("Failed to load " + EXTERNAL_PROPERTIES_PATH, var6);
      }
   }

   private static Properties loadDefaults() {
      Properties defaults = new Properties();

      try {
         try (StringReader in = new StringReader(
               "# Enhanced Celestials mixin toggles.\n\n# Overrides Minecraft's default time command with a protected/educated version of the time commands.\n# /time set <day/midnight/noon/night/<time_value>> will now set the time to the next occurrence of that time for the next Minecraft day.\n# /time set <day/midnight/noon/night/<time_value>> now has a 3rd argument that is \"reset\" which is a <true/false> value that when set to true will make this command function like its vanilla counterpart, resetting the the day time counter back to the first MC day.\n# Output messages have been updated to be more clear about what the command is doing.\n# This is enabled by default, but can be disabled if this mixin conflicts with another mod.\nTimeCommandMixin=true\n"
            )) {
            defaults.load(in);
         }

         return defaults;
      } catch (IOException var6) {
         throw new RuntimeException("Failed to load default mixin properties", var6);
      }
   }

   public Properties getProperties() {
      return this.properties;
   }
}
