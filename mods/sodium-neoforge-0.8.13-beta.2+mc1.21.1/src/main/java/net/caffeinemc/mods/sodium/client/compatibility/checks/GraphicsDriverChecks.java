/*
 * Decompiled with CFR 0.152.
 */
package net.caffeinemc.mods.sodium.client.compatibility.checks;

import net.caffeinemc.mods.sodium.client.compatibility.checks.BugChecks;
import net.caffeinemc.mods.sodium.client.compatibility.environment.GlContextInfo;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.intel.IntelWorkarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia.NvidiaDriverVersion;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia.NvidiaWorkarounds;
import net.caffeinemc.mods.sodium.client.platform.NativeWindowHandle;
import net.caffeinemc.mods.sodium.client.platform.PlatformHelper;
import net.caffeinemc.mods.sodium.client.platform.windows.WindowsFileVersion;

class GraphicsDriverChecks {
    GraphicsDriverChecks() {
    }

    static void postContextInit(NativeWindowHandle window, GlContextInfo context) {
        String installedVersionString;
        WindowsFileVersion installedVersion;
        GraphicsAdapterVendor vendor = GraphicsAdapterVendor.fromContext(context);
        if (vendor == GraphicsAdapterVendor.UNKNOWN) {
            return;
        }
        if (vendor == GraphicsAdapterVendor.INTEL && BugChecks.ISSUE_899 && (installedVersion = IntelWorkarounds.findIntelDriverMatchingBug899()) != null) {
            installedVersionString = installedVersion.toString();
            PlatformHelper.showCriticalErrorAndClose(window, "Sodium Renderer - Unsupported Driver", "The game failed to start because the currently installed Intel Graphics Driver is not compatible.\n\nInstalled version: ###CURRENT_DRIVER###\nRequired version: 10.18.10.5161 (or newer)\n\nPlease click the 'Help' button to read more about how to fix this problem.".replace("###CURRENT_DRIVER###", installedVersionString), "https://link.caffeinemc.net/help/sodium/graphics-driver/windows/intel/gh-899");
        }
        if (vendor == GraphicsAdapterVendor.NVIDIA && BugChecks.ISSUE_1486 && (installedVersion = NvidiaWorkarounds.findNvidiaDriverMatchingBug1486()) != null) {
            installedVersionString = NvidiaDriverVersion.parse(installedVersion).toString();
            PlatformHelper.showCriticalErrorAndClose(window, "Sodium Renderer - Unsupported Driver", "The game failed to start because the currently installed NVIDIA Graphics Driver is not compatible.\n\nInstalled version: ###CURRENT_DRIVER###\nRequired version: 536.23 (or newer)\n\nPlease click the 'Help' button to read more about how to fix this problem.".replace("###CURRENT_DRIVER###", installedVersionString), "https://link.caffeinemc.net/help/sodium/graphics-driver/windows/nvidia/gh-1486");
        }
    }
}

