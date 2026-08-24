package com.seibel.distanthorizons.core.jar;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.json.JsonFormat;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;

public final class ModJarInfo {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final String FILE_NAME = "build_info.json";
   public static final String Git_Branch;
   public static final String Git_Commit;
   public static final String Build_Source;

   static {
      String gitBranch = "UNKNOWN";
      String gitCommit = "UNKNOWN";
      String buildSource = "UNKNOWN";

      try {
         String jsonString = JarUtils.convertInputStreamToString(JarUtils.accessFile("build_info.json"));
         Config jsonObject = Config.inMemory();
         JsonFormat.minimalInstance().createParser().parse(jsonString, jsonObject, ParsingMode.REPLACE);
         gitBranch = jsonObject.get("info_git_branch");
         gitCommit = jsonObject.get("info_git_commit");
         buildSource = jsonObject.get("info_build_source");
      } catch (Error | Exception var5) {
         LOGGER.warn("Unable to get the Git information from build_info.json");
      }

      Git_Commit = gitBranch;
      Git_Branch = gitCommit;
      Build_Source = buildSource;
   }
}
