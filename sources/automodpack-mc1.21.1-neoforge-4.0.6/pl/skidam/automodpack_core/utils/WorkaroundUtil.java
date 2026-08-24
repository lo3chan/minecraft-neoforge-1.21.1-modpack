package pl.skidam.automodpack_core.utils;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.Jsons;

public class WorkaroundUtil {
   public final Path modpackPath;

   public WorkaroundUtil(Path modapckPath) {
      this.modpackPath = modapckPath;
   }

   public Set<String> getWorkaroundMods(Jsons.ModpackContentFields modpackContentFields) {
      Set<String> workaroundMods = new HashSet<>();
      if (GlobalVariables.LOADER != null && GlobalVariables.LOADER.contains("forge")) {
         for (Jsons.ModpackContentFields.ModpackContentItem item : modpackContentFields.list) {
            if (item.type.equals("mod")) {
               Path modPath = CustomFileUtils.getPath(this.modpackPath, item.file);
               if (FileInspection.hasSpecificServices(modPath)) {
                  workaroundMods.add(item.file);
               }
            }
         }

         return workaroundMods;
      } else {
         return workaroundMods;
      }
   }
}
