package pl.skidam.automodpack_core.loader;

import java.nio.file.Path;
import java.util.List;
import pl.skidam.automodpack_core.utils.FileInspection;

public interface ModpackLoaderService {
   void loadModpack(List<Path> var1);

   List<FileInspection.Mod> getModpackNestedConflicts(Path var1);
}
