package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import java.nio.file.Path;

public final class CommentedFileConfigBuilder extends GenericBuilder<CommentedConfig, CommentedFileConfig> {
   CommentedFileConfigBuilder(Path file, ConfigFormat<? extends CommentedConfig> format) {
      super(file, format);
   }
}
