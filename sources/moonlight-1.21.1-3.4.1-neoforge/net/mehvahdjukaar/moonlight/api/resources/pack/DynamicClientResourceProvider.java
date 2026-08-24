package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.nio.file.Files;
import java.nio.file.Path;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public abstract class DynamicClientResourceProvider extends DynamicResourcesProvider {
   protected DynamicClientResourceProvider(ResourceLocation name, PackGenerationStrategy generationPolicy) {
      super(name, PackType.CLIENT_RESOURCES, generationPolicy);
      if (PlatHelper.getPhysicalSide().isServer()) {
         throw new IllegalStateException("Client only class registered on server side! Issue from mod" + name);
      } else {
         MoonlightEventsHelper.addListener(this::addDynamicTranslations, AfterLanguageLoadEvent.class);
         Path logoPath = ClientHelper.getModIcon(name.getNamespace());
         if (logoPath != null) {
            try {
               this.packResources.addRootResource("pack.png", Files.readAllBytes(logoPath));
            } catch (Exception var5) {
            }
         }
      }
   }

   protected abstract void addDynamicTranslations(AfterLanguageLoadEvent var1);
}
