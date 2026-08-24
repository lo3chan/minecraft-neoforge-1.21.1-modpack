package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@FunctionalInterface
public interface AddDataPackReloadListenersCallback {
   EventInvoker<AddDataPackReloadListenersCallback> EVENT = EventInvoker.lookup(AddDataPackReloadListenersCallback.class);

   void onAddDataPackReloadListeners(BiConsumer<ResourceLocation, BiFunction<Provider, RegistryAccess, PreparableReloadListener>> var1);
}
