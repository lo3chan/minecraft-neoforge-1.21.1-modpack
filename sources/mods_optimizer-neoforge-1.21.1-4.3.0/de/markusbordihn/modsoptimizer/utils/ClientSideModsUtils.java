package de.markusbordihn.modsoptimizer.utils;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.data.ModFileData;
import java.io.File;
import java.util.Set;

public class ClientSideModsUtils {
   public static final String CLIENT_MOD_EXTENSION = ".client";
   private static final String LOG_PREFIX = "[Client Side Mod]";

   protected ClientSideModsUtils() {
   }

   public static int enable(File modPath) {
      int result = 0;
      if (modPath != null && modPath.exists()) {
         File[] modsFiles = modPath.listFiles();
         if (modsFiles != null && modsFiles.length != 0) {
            for (File modFile : modsFiles) {
               String modFileName = modFile.getName();
               if (modFileName.endsWith(".client")) {
                  File clientFile = new File(modFile.getAbsoluteFile().toString().replace(".jar.client", ".jar"));
                  Constants.LOG.info("{} ✔ Try to enable client side mod {} ...", "[Client Side Mod]", modFileName);
                  if (clientFile.exists()) {
                     if (!ModFileUtils.deleteModFile(modFile)) {
                        Constants.LOG.error("{} ⚠ Was unable to remove duplicated client side mod {}!", "[Client Side Mod]", modFile);
                     } else {
                        Constants.LOG.info("{} ✔ Removed duplicated client side mod {}!", "[Client Side Mod]", modFile);
                        result++;
                     }
                  } else if (!modFile.renameTo(clientFile)) {
                     Constants.LOG.error("{} ⚠ Was unable to enable client side mod {}!", "[Client Side Mod]", modFile);
                  } else {
                     Constants.LOG.info("{} ✔ Enabled client side mod {}!", "[Client Side Mod]", modFileName);
                     result++;
                  }
               }
            }

            return result;
         } else {
            Constants.LOG.error("{} unable to find valid mod files in path: {}", "[Client Side Mod]", modPath);
            return result;
         }
      } else {
         Constants.LOG.error("{} unable to find valid mod path: {}", "[Client Side Mod]", modPath);
         return result;
      }
   }

   public static int disable(Set<ModFileData> modFiles) {
      int result = 0;
      if (modFiles != null && !modFiles.isEmpty()) {
         for (ModFileData modFileData : modFiles) {
            if (modFileData.environment() == ModFileData.ModEnvironment.CLIENT) {
               File modFile = modFileData.path().toFile();
               File clientFile = new File(modFile.getAbsoluteFile() + ".client");
               Constants.LOG.info("{} ❌ Try to disable client side mod {} ...", "[Client Side Mod]", modFileData.id());
               if (clientFile.exists()) {
                  if (!ModFileUtils.deleteModFile(clientFile)) {
                     Constants.LOG
                        .error("{} ⚠ Was unable to remove client side mod {} with {}!", new Object[]{"[Client Side Mod]", modFileData.id(), clientFile});
                  } else {
                     Constants.LOG.info("{} ✔ Removed duplicated client side mod {} with {}!", new Object[]{"[Client Side Mod]", modFileData.id(), clientFile});
                     result++;
                  }
               } else if (!modFile.renameTo(clientFile)) {
                  Constants.LOG.error("{} ⚠ Was unable to disable client side mod {}!", "[Client Side Mod]", modFile);
               } else {
                  Constants.LOG.info("{} ✔ Disabled client side mod {}!", "[Client Side Mod]", modFileData.id());
                  result++;
               }
            } else {
               Constants.LOG
                  .info("{} ❌ Skip wrongly client side mod {} with {}!", new Object[]{"[Client Side Mod]", modFileData.id(), modFileData.environment()});
            }
         }

         return result;
      } else {
         return result;
      }
   }
}
