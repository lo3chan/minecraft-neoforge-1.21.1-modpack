package net.caffeinemc.mods.sodium.client.compatibility.checks;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import net.caffeinemc.mods.sodium.client.platform.PlatformHelper;
import org.lwjgl.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreLaunchChecks {
   private static final Logger LOGGER = LoggerFactory.getLogger("Sodium-PreLaunchChecks");
   private static final String REQUIRED_LWJGL_VERSION = "3.3.3";

   public static void checkEnvironment() {
      if (BugChecks.ISSUE_2561) {
         checkLwjglRuntimeVersion();
      }
   }

   private static void checkLwjglRuntimeVersion() {
      if (!isUsingKnownCompatibleLwjglVersion()) {
         String launcher = getLauncherBrand();
         String codeSource = getLwjglCodeSource();
         String codeSourceFilename = null;
         if (codeSource != null) {
            String[] components = codeSource.split("/");
            codeSourceFilename = components[components.length - 1];
         }

         if (codeSource != null) {
            LOGGER.info("Problematic LWJGL version source: {}", codeSource);
         }

         boolean isCustomLauncher = !launcher.equals("minecraft-launcher") && !launcher.equals("unknown");
         boolean isLikelyCausedByLauncher = false;
         String isLikelyCausedByMod = null;
         if (isCustomLauncher && codeSourceFilename != null && (codeSourceFilename.startsWith("lwjgl-") || codeSource.contains("/lwjgl/"))) {
            isLikelyCausedByLauncher = true;
         }

         if (!isLikelyCausedByLauncher && codeSource != null && codeSource.endsWith("/mods/" + codeSourceFilename)) {
            isLikelyCausedByMod = codeSourceFilename;
         }

         String advice;
         if (isLikelyCausedByMod != null) {
            advice = "This issue seems to be caused by ###MOD###.\n\nRemoving ###MOD### from your mods folder may fix this issue."
               .replace("###MOD###", isLikelyCausedByMod);
         } else if (launcher.equalsIgnoreCase("prismlauncher")) {
            advice = "It appears you are using Prism Launcher to start the game. You can likely fix this problem by opening your instance settings and navigating to the Version section in the sidebar.";
         } else if (isLikelyCausedByLauncher) {
            advice = "You seem to be using ###LAUNCHER###. This issue is likely caused by ###LAUNCHER###.\n\nYou must change the LWJGL version in your launcher to continue. This is usually controlled by the settings for a profile or instance in your launcher.\n\nIf you need assistance fixing the LWJGL version, you should contact ###LAUNCHER###, not Sodium."
               .replace("###LAUNCHER###", launcher);
         } else if (isCustomLauncher) {
            advice = "You seem to be using ###LAUNCHER###.\n\nYou must change the LWJGL version in your launcher to continue. This is usually controlled by the settings for a profile or instance in your launcher."
               .replace("###LAUNCHER###", launcher);
         } else {
            advice = "You must change the LWJGL version in your launcher to continue. This is usually controlled by the settings for a profile or instance in your launcher.";
         }

         String message = "The game failed to start because the currently active LWJGL version is not compatible.\n\nInstalled version: ###CURRENT_VERSION###\nRequired version: ###REQUIRED_VERSION###\n\n###ADVICE_STRING###"
            .replace("###CURRENT_VERSION###", Version.getVersion())
            .replace("###REQUIRED_VERSION###", "3.3.3")
            .replace("###ADVICE_STRING###", advice);
         PlatformHelper.showCriticalErrorAndClose(
            null, "Sodium Renderer - Unsupported LWJGL", message, "https://link.caffeinemc.net/help/sodium/runtime-issue/lwjgl3/gh-2561"
         );
      }
   }

   private static String getLwjglCodeSource() {
      try {
         ProtectionDomain domain = Version.class.getProtectionDomain();
         if (domain != null) {
            CodeSource source = domain.getCodeSource();
            if (source != null) {
               URL location = source.getLocation();
               if (location != null) {
                  String path = location.getPath();
                  if (path != null) {
                     path = path.replace('\\', '/');
                     return path.split("!")[0];
                  }
               }
            }
         }
      } catch (Throwable var4) {
         LOGGER.error("Error while checking code source of LWJGL", var4);
      }

      return null;
   }

   private static boolean isUsingKnownCompatibleLwjglVersion() {
      return Version.getVersion().startsWith("3.3.3");
   }

   private static String getLauncherBrand() {
      String brand = System.getProperty("minecraft.launcher.brand", "unknown");
      if (brand.equals("unknown")) {
         if (isClassLoaded("com.moonsworth.lunar.genesis.Genesis")) {
            return "Lunar Client";
         }

         if (System.getProperty("lunar.webosr.url") != null) {
            return "Lunar Client";
         }
      }

      return brand;
   }

   private static boolean isClassLoaded(String className) {
      try {
         Class.forName(className);
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }
}
