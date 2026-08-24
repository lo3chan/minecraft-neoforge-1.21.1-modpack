package net.caffeinemc.mods.sodium.client.compatibility.workarounds.amd;

import net.caffeinemc.mods.sodium.client.compatibility.environment.OsUtils;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterProbe;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia.NvidiaWorkarounds;
import net.caffeinemc.mods.sodium.client.platform.windows.WindowsCommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmdWorkarounds {
   private static final Logger LOGGER = LoggerFactory.getLogger("Sodium-AmdWorkarounds");

   public static boolean isAmdGraphicsCardPresent() {
      return GraphicsAdapterProbe.getAdapters().stream().anyMatch(adapter -> adapter.vendor() == GraphicsAdapterVendor.AMD);
   }

   public static void undoEnvironmentChanges() {
      if (OsUtils.getOs() == OsUtils.OperatingSystem.WIN) {
         undoEnvironmentChanges$Windows();
      }
   }

   private static void undoEnvironmentChanges$Windows() {
      WindowsCommandLine.resetCommandLine();
   }

   public static void applyEnvironmentChanges() {
      if (isAmdGraphicsCardPresent()) {
         if (!NvidiaWorkarounds.isNvidiaGraphicsCardPresent()) {
            try {
               if (OsUtils.getOs() == OsUtils.OperatingSystem.WIN) {
                  LOGGER.info("Modifying process environment to apply workarounds for the AMD graphics driver...");
                  applyEnvironmentChanges$Windows();
               }
            } catch (Throwable var1) {
               LOGGER.error("Failed to modify the process environment", var1);
               logWarning();
            }
         }
      }
   }

   private static void applyEnvironmentChanges$Windows() {
      WindowsCommandLine.setCommandLine("net.caffeinemc.sodium / net.minecraft.client.main.Main /");
   }

   private static void logWarning() {
      LOGGER.error("READ ME!");
      LOGGER.error("READ ME! The workarounds for the AMD Graphics Driver did not apply correctly!");
      LOGGER.error("READ ME! You may run into unexplained graphical issues.");
      LOGGER.error("READ ME! More information about what went wrong can be found above this message.");
      LOGGER.error("READ ME!");
      LOGGER.error("READ ME! Please help us understand why this problem occurred by opening a bug report on our issue tracker:");
      LOGGER.error("READ ME!   https://github.com/CaffeineMC/sodium/issues");
      LOGGER.error("READ ME!");
   }
}
