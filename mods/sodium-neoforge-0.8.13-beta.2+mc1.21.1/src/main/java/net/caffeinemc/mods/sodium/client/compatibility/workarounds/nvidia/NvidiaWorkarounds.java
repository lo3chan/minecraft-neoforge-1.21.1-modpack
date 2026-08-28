/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.opengl.GLCapabilities
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia;

import net.caffeinemc.mods.sodium.client.compatibility.environment.GlContextInfo;
import net.caffeinemc.mods.sodium.client.compatibility.environment.OsUtils;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterInfo;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterProbe;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.Workarounds;
import net.caffeinemc.mods.sodium.client.platform.unix.Libc;
import net.caffeinemc.mods.sodium.client.platform.windows.WindowsCommandLine;
import net.caffeinemc.mods.sodium.client.platform.windows.WindowsFileVersion;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMT;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NvidiaWorkarounds {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Sodium-NvidiaWorkarounds");

    public static boolean isNvidiaGraphicsCardPresent() {
        return GraphicsAdapterProbe.getAdapters().stream().anyMatch(adapter -> adapter.vendor() == GraphicsAdapterVendor.NVIDIA);
    }

    @Nullable
    public static WindowsFileVersion findNvidiaDriverMatchingBug1486() {
        if (OsUtils.getOs() != OsUtils.OperatingSystem.WIN) {
            return null;
        }
        for (GraphicsAdapterInfo graphicsAdapterInfo : GraphicsAdapterProbe.getAdapters()) {
            D3DKMT.WDDMAdapterInfo wddmAdapterInfo;
            WindowsFileVersion driverVersion;
            if (graphicsAdapterInfo.vendor() != GraphicsAdapterVendor.NVIDIA || !(graphicsAdapterInfo instanceof D3DKMT.WDDMAdapterInfo) || (driverVersion = (wddmAdapterInfo = (D3DKMT.WDDMAdapterInfo)graphicsAdapterInfo).openglIcdVersion()).z() != 15 || driverVersion.w() < 2647 || driverVersion.w() >= 3623) continue;
            return driverVersion;
        }
        return null;
    }

    public static void applyEnvironmentChanges() {
        if (!NvidiaWorkarounds.isNvidiaGraphicsCardPresent()) {
            return;
        }
        LOGGER.info("Modifying process environment to apply workarounds for the NVIDIA graphics driver...");
        try {
            if (OsUtils.getOs() == OsUtils.OperatingSystem.WIN) {
                NvidiaWorkarounds.applyEnvironmentChanges$Windows();
            } else if (OsUtils.getOs() == OsUtils.OperatingSystem.LINUX) {
                NvidiaWorkarounds.applyEnvironmentChanges$Linux();
            }
        }
        catch (Throwable t) {
            LOGGER.error("Failed to modify the process environment", t);
            NvidiaWorkarounds.logWarning();
        }
    }

    private static void applyEnvironmentChanges$Windows() {
        WindowsCommandLine.setCommandLine("net.caffeinemc.sodium / net.minecraft.client.main.Main /");
    }

    private static void applyEnvironmentChanges$Linux() {
        Libc.setEnvironmentVariable("__GL_THREADED_OPTIMIZATIONS", "0");
    }

    public static void undoEnvironmentChanges() {
        if (OsUtils.getOs() == OsUtils.OperatingSystem.WIN) {
            NvidiaWorkarounds.undoEnvironmentChanges$Windows();
        }
    }

    private static void undoEnvironmentChanges$Windows() {
        WindowsCommandLine.resetCommandLine();
    }

    public static void applyContextChanges(GlContextInfo context) {
        if (GraphicsAdapterVendor.fromContext(context) != GraphicsAdapterVendor.NVIDIA) {
            return;
        }
        LOGGER.info("Modifying OpenGL context to apply workarounds for the NVIDIA graphics driver...");
        if (Workarounds.isWorkaroundEnabled(Workarounds.Reference.NVIDIA_THREADED_OPTIMIZATIONS_BROKEN) && OsUtils.getOs() == OsUtils.OperatingSystem.WIN) {
            NvidiaWorkarounds.applyContextChanges$Windows();
        }
    }

    private static void applyContextChanges$Windows() {
        GLCapabilities capabilities = GL.getCapabilities();
        if (capabilities.GL_KHR_debug) {
            LOGGER.info("Enabling GL_DEBUG_OUTPUT_SYNCHRONOUS to force the NVIDIA driver to disable threaded command submission");
            GL32C.glEnable((int)33346);
        } else {
            LOGGER.error("GL_KHR_debug does not appear to be supported, unable to disable threaded command submission!");
            NvidiaWorkarounds.logWarning();
        }
    }

    private static void logWarning() {
        LOGGER.error("READ ME!");
        LOGGER.error("READ ME! The workarounds for the NVIDIA Graphics Driver did not apply correctly!");
        LOGGER.error("READ ME! You are very likely going to run into unexplained crashes and severe performance issues.");
        LOGGER.error("READ ME! More information about what went wrong can be found above this message.");
        LOGGER.error("READ ME!");
        LOGGER.error("READ ME! Please help us understand why this problem occurred by opening a bug report on our issue tracker:");
        LOGGER.error("READ ME!   https://github.com/CaffeineMC/sodium/issues");
        LOGGER.error("READ ME!");
    }
}

