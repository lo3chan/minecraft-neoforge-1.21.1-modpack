package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import java.nio.file.Path;

public class FileConfigBuilder extends GenericBuilder<Config, FileConfig> {
   FileConfigBuilder(Path file, ConfigFormat<? extends Config> format) {
      super(file, format);
   }
}
