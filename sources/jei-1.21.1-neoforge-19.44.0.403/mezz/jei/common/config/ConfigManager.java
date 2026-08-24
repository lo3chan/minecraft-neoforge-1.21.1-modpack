package mezz.jei.common.config;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mezz.jei.api.runtime.config.IJeiConfigFile;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import mezz.jei.common.config.file.ConfigSchema;
import org.jetbrains.annotations.Unmodifiable;

public class ConfigManager implements IJeiConfigManager {
   private final Map<Path, ConfigSchema> configFiles = new HashMap<>();

   public void registerConfigFile(ConfigSchema configFile) {
      this.configFiles.put(configFile.getPath(), configFile);
   }

   @Unmodifiable
   @Override
   public Collection<IJeiConfigFile> getConfigFiles() {
      return Collections.unmodifiableCollection(this.configFiles.values());
   }

   public void onJeiStarted() {
      this.configFiles.values().forEach(ConfigSchema::markDirty);
   }
}
