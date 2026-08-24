package com.seibel.distanthorizons.fabric.wrappers.modAccessor;

import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import java.io.File;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModChecker implements IModChecker {
   public static final ModChecker INSTANCE = new ModChecker();

   @Override
   public boolean isModLoaded(String modid) {
      return FabricLoader.getInstance().isModLoaded(modid);
   }

   @Override
   public File modLocation(String modid) {
      return new File(((Path)((ModContainer)FabricLoader.getInstance().getModContainer(modid).get()).getOrigin().getPaths().get(0)).toUri());
   }
}
