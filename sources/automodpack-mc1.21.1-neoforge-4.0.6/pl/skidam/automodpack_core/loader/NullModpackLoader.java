package pl.skidam.automodpack_core.loader;

import java.nio.file.Path;
import java.util.List;
import pl.skidam.automodpack_core.utils.FileInspection;

public class NullModpackLoader implements ModpackLoaderService {
   @Override
   public void loadModpack(List<Path> modpackMods) {
      throw new AssertionError("Loader class not found");
   }

   @Override
   public List<FileInspection.Mod> getModpackNestedConflicts(Path modpackDir) {
      throw new AssertionError("Loader class not found");
   }
}
