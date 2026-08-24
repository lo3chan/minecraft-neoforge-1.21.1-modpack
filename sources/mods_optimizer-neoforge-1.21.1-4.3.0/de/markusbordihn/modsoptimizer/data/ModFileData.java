package de.markusbordihn.modsoptimizer.data;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import java.nio.file.Path;
import java.time.LocalDateTime;

public record ModFileData(
   Path path, String id, ModFileData.ModType modType, String name, Version version, ModFileData.ModEnvironment environment, LocalDateTime timestamp
) {
   public static final Version EMPTY_VERSION = Version.valueOf("0.0.0");
   public static final String EMPTY_MOD_ID = "unknown_id";
   public static final String EMPTY_MOD_NAME = "Unknown";
   public static final LocalDateTime EMPTY_TIMESTAMP = LocalDateTime.now();

   public static enum ModEnvironment {
      BOTH,
      CLIENT,
      SERVER,
      SERVICE,
      LIBRARY,
      LANGUAGE_PROVIDER,
      DATA_PACK,
      UNKNOWN;
   }

   public static enum ModType {
      FABRIC,
      FORGE,
      NEOFORGE,
      MIXED,
      QUILT,
      UNKNOWN;
   }
}
