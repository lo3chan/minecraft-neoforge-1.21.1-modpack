package me.shedaniel.autoconfig.util.neoforge;

import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;

public class UtilsImpl {
   public static Path getConfigFolder() {
      return FMLPaths.CONFIGDIR.get();
   }
}
