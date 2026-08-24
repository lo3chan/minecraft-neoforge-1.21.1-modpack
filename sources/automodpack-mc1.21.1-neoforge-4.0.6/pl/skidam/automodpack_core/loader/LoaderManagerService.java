package pl.skidam.automodpack_core.loader;

import java.util.Collection;
import pl.skidam.automodpack_core.utils.FileInspection;

public interface LoaderManagerService {
   LoaderManagerService.ModPlatform getPlatformType();

   Collection<FileInspection.Mod> getModList();

   boolean isModLoaded(String var1);

   String getLoaderVersion();

   LoaderManagerService.EnvironmentType getEnvironmentType();

   boolean isDevelopmentEnvironment();

   String getModVersion(String var1);

   public static enum EnvironmentType {
      CLIENT,
      SERVER,
      UNIVERSAL;
   }

   public static enum ModPlatform {
      FABRIC,
      QUILT,
      FORGE,
      NEOFORGE;
   }
}
