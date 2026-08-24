package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@FunctionalInterface
public interface AddResourcePackReloadListenersCallback {
   EventInvoker<AddResourcePackReloadListenersCallback> EVENT = EventInvoker.lookup(AddResourcePackReloadListenersCallback.class);

   void onAddResourcePackReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> var1);
}
