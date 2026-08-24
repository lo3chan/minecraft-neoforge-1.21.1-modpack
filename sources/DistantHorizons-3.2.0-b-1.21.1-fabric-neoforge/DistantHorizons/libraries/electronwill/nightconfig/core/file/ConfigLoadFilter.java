package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;

@FunctionalInterface
public interface ConfigLoadFilter {
   boolean acceptNewVersion(CommentedConfig commentedConfig);
}
