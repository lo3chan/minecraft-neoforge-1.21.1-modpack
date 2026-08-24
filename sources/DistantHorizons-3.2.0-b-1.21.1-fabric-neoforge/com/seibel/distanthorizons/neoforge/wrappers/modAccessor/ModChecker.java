package com.seibel.distanthorizons.neoforge.wrappers.modAccessor;

import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import java.io.File;
import net.neoforged.fml.ModList;

public class ModChecker implements IModChecker {
   public static final ModChecker INSTANCE = new ModChecker();

   @Override
   public boolean isModLoaded(String modid) {
      return ModList.get().isLoaded(modid);
   }

   @Override
   public File modLocation(String modid) {
      return ModList.get().getModFileById(modid).getFile().getFilePath().toFile();
   }
}
