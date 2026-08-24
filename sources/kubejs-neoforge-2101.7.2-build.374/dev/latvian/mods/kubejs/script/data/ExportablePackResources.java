package dev.latvian.mods.kubejs.script.data;

import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.server.packs.PackResources;

public interface ExportablePackResources extends PackResources {
   default String exportPath() {
      return this.packId();
   }

   void export(Path root) throws IOException;
}
