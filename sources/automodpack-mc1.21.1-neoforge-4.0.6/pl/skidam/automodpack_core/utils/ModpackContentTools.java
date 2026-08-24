package pl.skidam.automodpack_core.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.Jsons;

public class ModpackContentTools {
   public static String getFileType(String file, Jsons.ModpackContentFields list) {
      for (Jsons.ModpackContentFields.ModpackContentItem item : list.list) {
         if (item.file.contains(file)) {
            return item.type;
         }
      }

      return "other";
   }

   public static Optional<Path> getModpackDir(String modpack) {
      return modpack != null && !modpack.isEmpty() ? Optional.of(GlobalVariables.modpacksDir.resolve(modpack)) : Optional.empty();
   }

   public static Optional<Path> getModpackContentFile(Path modpackDir) {
      if (!Files.exists(modpackDir)) {
         return Optional.empty();
      } else {
         Path path = modpackDir.getParent().resolve(GlobalVariables.hostModpackContentFile.getFileName());
         if (!Files.exists(path)) {
            path = modpackDir.resolve(GlobalVariables.hostModpackContentFile.getFileName());
            if (!Files.exists(path)) {
               return Optional.empty();
            }
         }

         return Optional.of(path);
      }
   }
}
