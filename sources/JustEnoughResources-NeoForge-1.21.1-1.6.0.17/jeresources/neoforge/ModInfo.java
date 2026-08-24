package jeresources.neoforge;

import java.util.List;
import jeresources.platform.IModInfo;
import net.minecraft.server.packs.PackResources;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.IModFileInfo;

public class ModInfo implements IModInfo {
   private IModFileInfo modFile;

   public ModInfo(IModFileInfo modFile) {
      this.modFile = modFile;
   }

   @Override
   public String getName() {
      return this.modFile.moduleName();
   }

   @Override
   public List<? extends PackResources> getPackResources() {
      return List.of(ResourcePackLoader.createPackForMod(this.modFile).openPrimary(null));
   }
}
