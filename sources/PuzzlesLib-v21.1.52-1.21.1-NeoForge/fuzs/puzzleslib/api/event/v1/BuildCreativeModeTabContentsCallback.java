package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;

@FunctionalInterface
public interface BuildCreativeModeTabContentsCallback {
   static EventInvoker<BuildCreativeModeTabContentsCallback> buildCreativeModeTabContents(ResourceKey<CreativeModeTab> resourceKey) {
      Objects.requireNonNull(resourceKey, "resource key is null");
      return EventInvoker.lookup(BuildCreativeModeTabContentsCallback.class, resourceKey);
   }

   void onBuildCreativeModeTabContents(CreativeModeTab var1, ItemDisplayParameters var2, Output var3);
}
