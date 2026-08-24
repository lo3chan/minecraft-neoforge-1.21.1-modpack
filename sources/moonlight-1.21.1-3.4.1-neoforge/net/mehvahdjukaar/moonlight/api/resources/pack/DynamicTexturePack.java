package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.nio.file.Files;
import java.nio.file.Path;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack.Position;

@Deprecated(
   forRemoval = true
)
public class DynamicTexturePack extends DynamicResourcePack {
   public DynamicTexturePack(ResourceLocation name, Position position, boolean fixed, boolean hidden) {
      super(name, PackType.CLIENT_RESOURCES, position, fixed, hidden);
   }

   public DynamicTexturePack(ResourceLocation name) {
      super(name, PackType.CLIENT_RESOURCES);
   }

   @Override
   public void registerPack() {
      this.addIcon();
      super.registerPack();
   }

   private void addIcon() {
      Path logoPath = ClientHelper.getModIcon(this.mainNamespace);
      if (logoPath != null) {
         try {
            this.addRootResource("pack.png", Files.readAllBytes(logoPath));
         } catch (Exception var3) {
            Moonlight.LOGGER.error("Failed to load mod icon for{}", this.mainNamespace);
         }
      } else {
         Moonlight.LOGGER.error("Failed to find mod icon for{}", this.mainNamespace);
      }
   }
}
