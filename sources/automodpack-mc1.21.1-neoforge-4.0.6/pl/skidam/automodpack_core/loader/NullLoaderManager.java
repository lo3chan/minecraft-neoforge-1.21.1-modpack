package pl.skidam.automodpack_core.loader;

import java.util.Collection;
import pl.skidam.automodpack_core.utils.FileInspection;

public class NullLoaderManager implements LoaderManagerService {
   @Override
   public LoaderManagerService.ModPlatform getPlatformType() {
      return null;
   }

   @Override
   public boolean isModLoaded(String modId) {
      return false;
   }

   @Override
   public Collection<FileInspection.Mod> getModList() {
      return null;
   }

   @Override
   public String getLoaderVersion() {
      return null;
   }

   @Override
   public LoaderManagerService.EnvironmentType getEnvironmentType() {
      return null;
   }

   @Override
   public String getModVersion(String modId) {
      return null;
   }

   @Override
   public boolean isDevelopmentEnvironment() {
      return false;
   }
}
