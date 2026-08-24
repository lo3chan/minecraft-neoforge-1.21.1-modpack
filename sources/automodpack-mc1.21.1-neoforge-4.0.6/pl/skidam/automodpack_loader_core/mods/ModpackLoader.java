package pl.skidam.automodpack_loader_core.mods;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.loader.ModpackLoaderService;
import pl.skidam.automodpack_core.utils.FileInspection;

public class ModpackLoader implements ModpackLoaderService {
   public static String CONNECTOR_MODS_PROPERTY = "connector.additionalModLocations";
   public static List<Path> modsToLoad = new ArrayList<>();

   @Override
   public void loadModpack(List<Path> modpackMods) {
      try {
         for (Path modpackMod : modpackMods) {
            if (FileInspection.isModCompatible(modpackMod)) {
               modsToLoad.add(modpackMod);
            }
         }

         String paths = modpackMods.stream().map(Path::toString).collect(Collectors.joining(","));
         String finalMods = paths + "," + System.getProperty(CONNECTOR_MODS_PROPERTY, "");
         System.setProperty(CONNECTOR_MODS_PROPERTY, finalMods);
      } catch (Exception var4) {
         GlobalVariables.LOGGER.error("Error while loading modpack", var4);
      }
   }

   @Override
   public List<FileInspection.Mod> getModpackNestedConflicts(Path modpackDir) {
      return new ArrayList<>();
   }
}
