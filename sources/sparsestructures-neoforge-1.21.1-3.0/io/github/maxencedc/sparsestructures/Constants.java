package io.github.maxencedc.sparsestructures;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
   public static final String MOD_ID = "sparsestructures";
   public static final String MOD_NAME = "Sparse Structures";
   public static final Logger LOG = LoggerFactory.getLogger("Sparse Structures");
   public static final String CONFIG_RESOURCE_NAME = "sparse-structures-default-config.json5";
   public static final String CONFIG_FILENAME = "sparsestructures.json5";
   public static final Path CONFIG_FILE_PATH = Path.of("config", "sparsestructures.json5");
}
