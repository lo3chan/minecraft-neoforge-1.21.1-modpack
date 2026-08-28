/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.caffeinemc.mods.sodium.client.compatibility.checks;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import net.caffeinemc.mods.sodium.client.compatibility.checks.BugChecks;
import net.caffeinemc.mods.sodium.client.platform.PlatformHelper;
import org.lwjgl.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreLaunchChecks {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Sodium-PreLaunchChecks");
    private static final String REQUIRED_LWJGL_VERSION = "3.3.3";

    public static void checkEnvironment() {
        if (BugChecks.ISSUE_2561) {
            PreLaunchChecks.checkLwjglRuntimeVersion();
        }
    }

    private static void checkLwjglRuntimeVersion() {
        if (PreLaunchChecks.isUsingKnownCompatibleLwjglVersion()) {
            return;
        }
        String launcher = PreLaunchChecks.getLauncherBrand();
        String codeSource = PreLaunchChecks.getLwjglCodeSource();
        String codeSourceFilename = null;
        if (codeSource != null) {
            String[] components = codeSource.split("/");
            codeSourceFilename = components[components.length - 1];
        }
        if (codeSource != null) {
            LOGGER.info("Problematic LWJGL version source: {}", (Object)codeSource);
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
        String advice = isLikelyCausedByMod != null ? "This issue seems to be caused by ###MOD###.\n\nRemoving ###MOD### from your mods folder may fix this issue.".replace("###MOD###", isLikelyCausedByMod) : (launcher.equalsIgnoreCase("prismlauncher") ? "It appears you are using Prism Launcher to start the game. You can likely fix this problem by opening your instance settings and navigating to the Version section in the sidebar." : (isLikelyCausedByLauncher ? "You seem to be using ###LAUNCHER###. This issue is likely caused by ###LAUNCHER###.\n\nYou must change the LWJGL version in your launcher to continue. This is usually controlled by the settings for a profile or instance in your launcher.\n\nIf you need assistance fixing the LWJGL version, you should contact ###LAUNCHER###, not Sodium.".replace("###LAUNCHER###", launcher) : (isCustomLauncher ? "You seem to be using ###LAUNCHER###.\n\nYou must change the LWJGL version in your launcher to continue. This is usually controlled by the settings for a profile or instance in your launcher.".replace("###LAUNCHER###", launcher) : "You must change the LWJGL version in your launcher to continue. This is usually controlled by the settings for a profile or instance in your launcher.")));
        String message = "The game failed to start because the currently active LWJGL version is not compatible.\n\nInstalled version: ###CURRENT_VERSION###\nRequired version: ###REQUIRED_VERSION###\n\n###ADVICE_STRING###".replace("###CURRENT_VERSION###", Version.getVersion()).replace("###REQUIRED_VERSION###", REQUIRED_LWJGL_VERSION).replace("###ADVICE_STRING###", advice);
        PlatformHelper.showCriticalErrorAndClose(null, "Sodium Renderer - Unsupported LWJGL", message, "https://link.caffeinemc.net/help/sodium/runtime-issue/lwjgl3/gh-2561");
    }

    private static String getLwjglCodeSource() {
        try {
            String path;
            URL location;
            CodeSource source;
            ProtectionDomain domain = Version.class.getProtectionDomain();
            if (domain != null && (source = domain.getCodeSource()) != null && (location = source.getLocation()) != null && (path = location.getPath()) != null) {
                path = path.replace('\\', '/');
                path = path.split("!")[0];
                return path;
            }
        }
        catch (Throwable t) {
            LOGGER.error("Error while checking code source of LWJGL", t);
        }
        return null;
    }

    private static boolean isUsingKnownCompatibleLwjglVersion() {
        return Version.getVersion().startsWith(REQUIRED_LWJGL_VERSION);
    }

    private static String getLauncherBrand() {
        String brand = System.getProperty("minecraft.launcher.brand", "unknown");
        if (brand.equals("unknown")) {
            if (PreLaunchChecks.isClassLoaded("com.moonsworth.lunar.genesis.Genesis")) {
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
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

