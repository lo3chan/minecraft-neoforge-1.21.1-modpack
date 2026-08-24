package com.finndog.moogs_structures.config;

import com.finndog.moogs_structures.platform.Services;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface PlatformConfig {
   PlatformConfig INSTANCE = Services.load(PlatformConfig.class);

   Path getConfigDir();

   Map<String, String> getOptionalPackManifests();

   Map<String, String> getStructureSetJsons(String var1);

   List<String> getAllModIds();

   String getModName(String var1);
}
