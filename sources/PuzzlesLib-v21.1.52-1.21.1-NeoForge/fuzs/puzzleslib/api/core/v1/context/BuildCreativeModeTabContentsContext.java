package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;

@Deprecated
public interface BuildCreativeModeTabContentsContext {
   default void registerBuildListener(String modId, DisplayItemsGenerator itemsGenerator) {
      ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(modId, "main");
      this.registerBuildListener(resourceLocation, itemsGenerator);
   }

   default void registerBuildListener(ResourceLocation identifier, DisplayItemsGenerator itemsGenerator) {
      ResourceKey<CreativeModeTab> resourceKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, identifier);
      this.registerBuildListener(resourceKey, itemsGenerator);
   }

   void registerBuildListener(ResourceKey<CreativeModeTab> var1, DisplayItemsGenerator var2);
}
