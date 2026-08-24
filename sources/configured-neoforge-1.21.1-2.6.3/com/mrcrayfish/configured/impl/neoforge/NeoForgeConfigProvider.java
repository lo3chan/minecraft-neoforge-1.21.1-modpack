package com.mrcrayfish.configured.impl.neoforge;

import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.api.IModConfigProvider;
import com.mrcrayfish.configured.api.ModContext;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeConfigProvider implements IModConfigProvider {
   @Override
   public Set<IModConfig> getConfigurationsForMod(ModContext context) {
      Set<IModConfig> configs = new HashSet<>();
      addForgeConfigSetToMap(context, Type.CLIENT, configs::add);
      addForgeConfigSetToMap(context, Type.COMMON, configs::add);
      addForgeConfigSetToMap(context, Type.SERVER, configs::add);
      addForgeConfigSetToMap(context, Type.STARTUP, configs::add);
      return configs;
   }

   private static void addForgeConfigSetToMap(ModContext context, Type type, Consumer<IModConfig> consumer) {
      Set<ModConfig> configSet = ModConfigs.getConfigSet(type);
      Set<IModConfig> filteredConfigSets = configSet.stream()
         .filter(config -> config.getModId().equals(context.modId()) && config.getSpec() instanceof ModConfigSpec)
         .map(NeoForgeConfig::new)
         .collect(Collectors.toSet());
      filteredConfigSets.forEach(consumer);
   }
}
