package com.mrcrayfish.configured;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.configured.api.Environment;
import com.mrcrayfish.configured.platform.Services;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;

public class Config {
   private static final Pattern UUID_PATTERN = Pattern.compile("^[\\da-fA-F]{8}\\b-[\\da-fA-F]{4}\\b-[\\da-fA-F]{4}\\b-[\\da-fA-F]{4}\\b-[\\da-fA-F]{12}$");
   private static final String VALID_CHAT_FORMATTING = (String)Util.make(() -> {
      StringJoiner joiner = new StringJoiner(", ");
      List.of(ChatFormatting.values()).forEach(formatting -> joiner.add(formatting.getName()));
      return joiner.toString();
   });
   private static final String DEFAULT_CLIENT_CONFIG = "# -------- CONFIGURED CLIENT CONFIG --------\n# If properties are missing, delete this file\n# and load the game to regenerate the config.\n\n# [Force Configured Menu]\n# If enabled, will attempt to override config screens provided by\n# other mods in favour for Configured's auto generated screen. May\n# not works for all mods.\n# Possible values: true, false\nforceConfiguredMenu=false\n\n# [Include Folders in Search]\n# When using the search bar in the config screen, the results will\n# also include folders and not just config properties.\n# Possible values: true, false\nincludeFoldersInSearch=false\n\n# [Changed Formatting]\n# The chat formatting of a config property name when it has changed\n# during editing.\n# Possible values: %s\nchangedFormatting=ITALIC\n"
      .formatted(VALID_CHAT_FORMATTING);
   private static final String DEFAULT_DEVELOPER_CONFIG = "# ------- CONFIGURED DEVELOPER CONFIG -------\n# If properties are missing, delete this file\n# and load the game to regenerate the config.\n\n# [Developer Mode]\n# Enables the ability to update remote configs. You should only enabled\n# developer mode when you are developing your server. This mode should not\n# be enabled if you are running in production, a public server, or a server\n# with players you don't trust. Even if this mode is enabled, you will still\n# need to authorise players who have access to editing remote configs by\n# listing them in the developers property below and they must also have\n# operator privileges. Only config systems that support remote editing will\n# be able to be edited.\n# Possible values: true, false\ndeveloperMode=false\n\n# [Broadcast Logs]\n# When a remote config is updated by a developer, broadcast those changes,\n# successful or not, into the chat of other developers. The log is also included\n# in the normal log of the game.\n# Possible values: true, false\nbroadcastLogs=true\n\n# [Developers]\n# A list of comma separated UUIDS of players who are authorised to edit remote\n# configs. The players must also have operator privileges.\n# You can find the UUID of a player in the log file when they join your server.\ndevelopers=\n";
   private static boolean forceConfiguredMenu = false;
   private static boolean includeFoldersInSearch = false;
   private static ChatFormatting changedFormatting = ChatFormatting.ITALIC;
   private static boolean developerMode = false;
   private static boolean broadcastLogs = false;
   private static ImmutableList<UUID> developers = ImmutableList.of();

   public static void load(Path path) {
      Environment environment = Services.PLATFORM.getEnvironment();
      if (environment == Environment.CLIENT) {
         loadConfigFile(path, "configured-client.properties", DEFAULT_CLIENT_CONFIG).ifPresent(file -> {
            try {
               Properties properties = new Properties();
               properties.load(new FileInputStream(file));
               forceConfiguredMenu = Boolean.parseBoolean(properties.getProperty("forceConfiguredMenu", "false"));
               includeFoldersInSearch = Boolean.parseBoolean(properties.getProperty("includeFoldersInSearch", "false"));
               changedFormatting = ChatFormatting.getByName(properties.getProperty("changedFormatting", "italic"));
            } catch (Exception var2) {
               throw new RuntimeException(var2);
            }
         });
      } else if (environment == Environment.DEDICATED_SERVER) {
         loadConfigFile(
               path,
               "configured-developer.properties",
               "# ------- CONFIGURED DEVELOPER CONFIG -------\n# If properties are missing, delete this file\n# and load the game to regenerate the config.\n\n# [Developer Mode]\n# Enables the ability to update remote configs. You should only enabled\n# developer mode when you are developing your server. This mode should not\n# be enabled if you are running in production, a public server, or a server\n# with players you don't trust. Even if this mode is enabled, you will still\n# need to authorise players who have access to editing remote configs by\n# listing them in the developers property below and they must also have\n# operator privileges. Only config systems that support remote editing will\n# be able to be edited.\n# Possible values: true, false\ndeveloperMode=false\n\n# [Broadcast Logs]\n# When a remote config is updated by a developer, broadcast those changes,\n# successful or not, into the chat of other developers. The log is also included\n# in the normal log of the game.\n# Possible values: true, false\nbroadcastLogs=true\n\n# [Developers]\n# A list of comma separated UUIDS of players who are authorised to edit remote\n# configs. The players must also have operator privileges.\n# You can find the UUID of a player in the log file when they join your server.\ndevelopers=\n"
            )
            .ifPresent(file -> {
               try {
                  Properties properties = new Properties();
                  properties.load(new FileInputStream(file));
                  developerMode = Boolean.parseBoolean(properties.getProperty("developerMode", "false"));
                  broadcastLogs = Boolean.parseBoolean(properties.getProperty("broadcastLogs", "true"));
                  developers = readDeveloperList(properties.getProperty("developers", ""));
               } catch (Exception var2) {
                  throw new RuntimeException(var2);
               }
            });
      }
   }

   private static ImmutableList<UUID> readDeveloperList(String value) {
      if (value.isBlank()) {
         return ImmutableList.of();
      } else {
         List<UUID> list = new ArrayList<>();

         for (String rawUuid : value.split(",")) {
            rawUuid = rawUuid.trim();
            if (!UUID_PATTERN.matcher(rawUuid).matches()) {
               Constants.LOG.error("Invalid UUID when loading developer config: {}", rawUuid);
            } else {
               list.add(UUID.fromString(rawUuid));
            }
         }

         return ImmutableList.copyOf(list);
      }
   }

   private static Optional<File> loadConfigFile(Path path, String name, String defaultConfig) {
      Path file = path.resolve(name);
      if (!Files.exists(file)) {
         try {
            Files.writeString(file, defaultConfig, StandardOpenOption.CREATE);
         } catch (IOException var5) {
            return Optional.empty();
         }
      }

      return Optional.of(file.toFile());
   }

   public static boolean isForceConfiguredMenu() {
      return forceConfiguredMenu;
   }

   public static boolean isIncludeFoldersInSearch() {
      return includeFoldersInSearch;
   }

   public static ChatFormatting getChangedFormatting() {
      return changedFormatting;
   }

   public static boolean isDeveloperEnabled() {
      return developerMode;
   }

   public static ImmutableList<UUID> getDevelopers() {
      return developers;
   }

   public static boolean shouldBroadcastLogs() {
      return broadcastLogs;
   }
}
