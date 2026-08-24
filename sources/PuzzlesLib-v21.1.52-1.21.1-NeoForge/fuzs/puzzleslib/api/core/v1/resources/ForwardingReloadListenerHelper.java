package fuzs.puzzleslib.api.core.v1.resources;

import fuzs.puzzleslib.impl.core.resources.ForwardingReloadListener;
import fuzs.puzzleslib.impl.core.resources.ForwardingResourceManagerReloadListener;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

@Deprecated
public final class ForwardingReloadListenerHelper {
   private ForwardingReloadListenerHelper() {
   }

   public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListener(
      ResourceLocation identifier, PreparableReloadListener reloadListener
   ) {
      return fromReloadListener(identifier, (Supplier<PreparableReloadListener>)(() -> reloadListener));
   }

   public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListener(
      ResourceLocation identifier, Supplier<PreparableReloadListener> supplier
   ) {
      return fromReloadListeners(identifier, () -> Collections.singletonList(supplier.get()));
   }

   public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListeners(
      ResourceLocation identifier, Collection<PreparableReloadListener> reloadListeners
   ) {
      return fromReloadListeners(identifier, () -> reloadListeners);
   }

   public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListeners(
      ResourceLocation identifier, Supplier<Collection<PreparableReloadListener>> supplier
   ) {
      return (T)(new ForwardingReloadListener<PreparableReloadListener>(identifier, supplier));
   }

   public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListener(
      ResourceLocation identifier, ResourceManagerReloadListener reloadListener
   ) {
      return fromResourceManagerReloadListener(identifier, (Supplier<ResourceManagerReloadListener>)(() -> reloadListener));
   }

   public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListener(
      ResourceLocation identifier, Supplier<ResourceManagerReloadListener> supplier
   ) {
      return fromResourceManagerReloadListeners(identifier, () -> Collections.singletonList(supplier.get()));
   }

   public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListeners(
      ResourceLocation identifier, Collection<ResourceManagerReloadListener> reloadListeners
   ) {
      return fromResourceManagerReloadListeners(identifier, () -> reloadListeners);
   }

   public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListeners(
      ResourceLocation identifier, Supplier<Collection<ResourceManagerReloadListener>> supplier
   ) {
      return (T)(new ForwardingResourceManagerReloadListener(identifier, supplier));
   }
}
