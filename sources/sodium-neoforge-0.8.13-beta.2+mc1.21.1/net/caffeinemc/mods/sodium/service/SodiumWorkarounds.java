package net.caffeinemc.mods.sodium.service;

import net.caffeinemc.mods.sodium.client.compatibility.checks.PreLaunchChecks;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterProbe;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.Workarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.amd.AmdWorkarounds;
import net.caffeinemc.mods.sodium.client.compatibility.workarounds.nvidia.NvidiaWorkarounds;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLConfig.ConfigValue;
import net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper;

public class SodiumWorkarounds implements GraphicsBootstrapper {
   public String name() {
      return "sodium";
   }

   public void bootstrap(String[] arguments) {
      PreLaunchChecks.checkEnvironment();
      GraphicsAdapterProbe.findAdapters();
      Workarounds.init();
      if (FMLConfig.getBoolConfigValue(ConfigValue.EARLY_WINDOW_CONTROL)) {
         NvidiaWorkarounds.applyEnvironmentChanges();
         AmdWorkarounds.applyEnvironmentChanges();
      }
   }
}
