package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ResourceManager;

@Deprecated(
   forRemoval = true
)
public abstract class DynClientResourcesGenerator extends DynResourceGenerator<DynamicTexturePack> {
   protected DynClientResourcesGenerator(DynamicTexturePack pack) {
      super(pack, pack.mainNamespace);
      if (PlatHelper.getPhysicalSide().isServer()) {
         throw new IllegalStateException("Client only class registered on server side! Issue from mod" + pack.mainNamespace);
      } else {
         MoonlightEventsHelper.addListener(this::addDynamicTranslations, AfterLanguageLoadEvent.class);
      }
   }

   @Override
   protected PackRepository getRepository() {
      return Minecraft.getInstance().getResourcePackRepository();
   }

   public void addDynamicTranslations(AfterLanguageLoadEvent languageEvent) {
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean alreadyHasTextureAtLocation(ResourceManager manager, ResourceLocation res) {
      return this.alreadyHasAssetAtLocation(manager, res, ResType.TEXTURES);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addTextureIfNotPresent(ResourceManager manager, String relativePath, Supplier<TextureImage> textureSupplier) {
      this.addTextureIfNotPresent(manager, relativePath, textureSupplier, true);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addTextureIfNotPresent(ResourceManager manager, String relativePath, Supplier<TextureImage> textureSupplier, boolean isOnAtlas) {
      ResourceLocation res = relativePath.contains(":")
         ? ResourceLocation.parse(relativePath)
         : ResourceLocation.fromNamespaceAndPath(this.modId, relativePath);
      if (!this.alreadyHasTextureAtLocation(manager, res)) {
         try (TextureImage textureImage = textureSupplier.get()) {
            this.dynamicPack.addAndCloseTexture(res, textureImage, isOnAtlas);
         } catch (Exception var11) {
            this.getLogger().error("Failed to generate texture {}: {}", res, var11);
            if (PlatHelper.isDev()) {
               throw new AssertionError(var11);
            }
         }
      }
   }
}
