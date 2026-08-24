package pl.skidam.automodpack_loader_core;

import cpw.mods.jarhandling.SecureJar;
import java.nio.file.Path;
import java.util.List;
import net.neoforged.fml.loading.moddiscovery.locators.JarInJarDependencyLocator;
import net.neoforged.fml.loading.moddiscovery.readers.JarModsDotTomlModFileReader;
import net.neoforged.neoforgespi.locating.IDependencyLocator;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFile;

public class LazyModLocator implements IDependencyLocator {
   public void scanMods(List<IModFile> loadedMods, IDiscoveryPipeline pipeline) {
      try {
         SecureJar secureJar = SecureJar.from(new Path[]{Path.of(LazyModLocator.class.getProtectionDomain().getCodeSource().getLocation().toURI())});
         IModFile modFile = IModFile.create(secureJar, JarModsDotTomlModFileReader::manifestParser);
         new JarInJarDependencyLocator().scanMods(List.of(modFile), pipeline);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   public int getPriority() {
      return -1000;
   }
}
